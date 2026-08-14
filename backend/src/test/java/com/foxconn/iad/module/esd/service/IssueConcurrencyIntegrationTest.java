package com.foxconn.iad.module.esd.service;

import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssueCreateReqVO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.PersonProfileDO;
import com.foxconn.iad.module.esd.dal.mysql.AssetMapper;
import com.foxconn.iad.module.esd.dal.mysql.IssueRecordMapper;
import com.foxconn.iad.module.esd.dal.mysql.PersonProfileMapper;
import com.foxconn.iad.module.esd.enums.AssetLifecycleStatus;
import com.foxconn.iad.module.esd.exception.BusinessException;
import com.foxconn.iad.module.esd.platform.PlatformDeptGateway;
import com.foxconn.iad.module.esd.platform.PlatformUserGateway;
import com.foxconn.iad.module.esd.security.LoginUserContext;
import com.foxconn.iad.module.esd.security.SiteAccessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * 發放交易的 MySQL 整合測試。
 *
 * <p>驗證條件更新在真實資料庫層的併發安全性：兩個併發請求發放同一資產，
 * 只有一個成功，另一個收到 409 狀態衝突。</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class IssueConcurrencyIntegrationTest {

    /** 廠區權限在測試中以 Mock 提供固定登入上下文，不依賴網關頭。 */
    @MockBean
    private SiteAccessService siteAccessService;
    /** 平台 RPC 以 Mock 隔離，整合測試不發起真實 Feign 調用。 */
    @MockBean
    private PlatformUserGateway platformUserGateway;
    @MockBean
    private PlatformDeptGateway platformDeptGateway;

    @Autowired
    private IssueService issueService;
    @Autowired
    private AssetMapper assetMapper;
    @Autowired
    private PersonProfileMapper personProfileMapper;
    @Autowired
    private IssueRecordMapper issueRecordMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        when(siteAccessService.requireSite("ZZ"))
                .thenReturn(new LoginUserContext(1L, 1L, Collections.singleton("ZZ"), false));
        cleanDatabase();
        personProfileMapper.insert(personProfile());
        assetMapper.insert(availableAsset());
    }

    @Test
    @DisplayName("兩個並發請求發放同一資產只有一個成功")
    void concurrentIssueOfSameAssetOnlyOneSucceeds() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger success = new AtomicInteger();
        AtomicInteger conflict = new AtomicInteger();
        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        Runnable task = () -> {
            try {
                start.await();
                issueService.create(issueRequest());
                success.incrementAndGet();
            } catch (BusinessException exception) {
                if (exception.getCode() == 409) {
                    conflict.incrementAndGet();
                } else {
                    errors.add(exception);
                }
            } catch (Exception exception) {
                errors.add(exception);
            }
        };

        pool.submit(task);
        pool.submit(task);
        start.countDown();
        pool.shutdown();
        assertThat(pool.awaitTermination(10, TimeUnit.SECONDS)).isTrue();

        // 恰好一次成功、一次狀態衝突，且資料庫只有一筆發放記錄。
        assertThat(success.get()).isEqualTo(1);
        assertThat(conflict.get()).isEqualTo(1);
        assertThat(errors).isEmpty();
        assertThat(issueRecordMapper.selectCount(null)).isEqualTo(1L);
    }

    /** 物理清理測試資料，繞過 MyBatis-Plus 邏輯刪除，避免固定主鍵累積。 */
    private void cleanDatabase() {
        jdbcTemplate.execute("DELETE FROM esd_asset_event");
        jdbcTemplate.execute("DELETE FROM esd_laundry_record");
        jdbcTemplate.execute("DELETE FROM esd_return_record");
        jdbcTemplate.execute("DELETE FROM esd_issue_record");
        jdbcTemplate.execute("DELETE FROM esd_asset");
        jdbcTemplate.execute("DELETE FROM esd_person_profile");
    }

    private PersonProfileDO personProfile() {
        PersonProfileDO profile = new PersonProfileDO();
        profile.setSiteCode("ZZ");
        profile.setPlatformUserId(200L);
        profile.setEmployeeNo("E001");
        profile.setEmployeeName("張三");
        profile.setDeptId(1L);
        profile.setDeptName("一課");
        profile.setSupervisorUserId(300L);
        profile.setSupervisorName("李四");
        profile.setFloorCode("2F");
        profile.setShiftCode("A班");
        profile.setEsdStatus(1);
        profile.setCreateTime(LocalDateTime.now());
        profile.setUpdateTime(LocalDateTime.now());
        return profile;
    }

    private AssetDO availableAsset() {
        AssetDO asset = new AssetDO();
        asset.setAssetCode("J-Y-001");
        asset.setSiteCode("ZZ");
        asset.setAssetType(1);
        asset.setColorCode("黃色");
        asset.setSizeCode("L");
        asset.setLifecycleStatus(AssetLifecycleStatus.AVAILABLE.getCode());
        asset.setCleanCount(0);
        asset.setVersion(0);
        asset.setCreateTime(LocalDateTime.now());
        asset.setUpdateTime(LocalDateTime.now());
        return asset;
    }

    private IssueCreateReqVO issueRequest() {
        IssueCreateReqVO request = new IssueCreateReqVO();
        request.setSiteCode("ZZ");
        request.setEmployeeUserId(200L);
        request.setAssetCode("J-Y-001");
        request.setIssueDate(LocalDate.of(2026, 8, 13));
        return request;
    }
}
