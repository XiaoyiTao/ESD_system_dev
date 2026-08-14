package com.foxconn.iad.module.esd.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssueCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.laundry.vo.LaundryRespVO;
import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnCreateReqVO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.LaundryRecordDO;
import com.foxconn.iad.module.esd.dal.dataobject.PersonProfileDO;
import com.foxconn.iad.module.esd.dal.mysql.AssetMapper;
import com.foxconn.iad.module.esd.dal.mysql.LaundryRecordMapper;
import com.foxconn.iad.module.esd.dal.mysql.PersonProfileMapper;
import com.foxconn.iad.module.esd.enums.AssetLifecycleStatus;
import com.foxconn.iad.module.esd.enums.BusinessRecordStatus;
import com.foxconn.iad.module.esd.enums.ReturnDisposition;
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
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

/**
 * 資產全生命週期的 MySQL 整合測試。
 *
 * <p>驗證入庫、發放、回收送洗、送洗登記、完成清洗的完整閉環，
 * 以及清洗次數只累加一次的冪等性。</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class AssetLifecycleFlowIntegrationTest {

    @MockBean
    private SiteAccessService siteAccessService;
    @MockBean
    private PlatformUserGateway platformUserGateway;
    @MockBean
    private PlatformDeptGateway platformDeptGateway;

    @Autowired
    private IssueService issueService;
    @Autowired
    private ReturnService returnService;
    @Autowired
    private LaundryService laundryService;
    @Autowired
    private AssetMapper assetMapper;
    @Autowired
    private PersonProfileMapper personProfileMapper;
    @Autowired
    private LaundryRecordMapper laundryRecordMapper;
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
    @DisplayName("入庫、發放、回收送洗、送洗登記、完成清洗形成閉環")
    void fullLifecycleFlowReturnsAssetToAvailableWithIncrementedCleanCount() {
        // 發放：庫存 -> 發放中。
        issueService.create(issueRequest());
        assertThat(assetStatus()).isEqualTo(AssetLifecycleStatus.ISSUED.getCode());

        // 回收送洗：發放中 -> 待送洗，並建立進行中清洗記錄。
        returnService.create(returnLaunderRequest());
        assertThat(assetStatus()).isEqualTo(AssetLifecycleStatus.PENDING_LAUNDRY.getCode());
        LaundryRecordDO laundry = activeLaundry();
        assertThat(laundry).isNotNull();
        assertThat(laundry.getSendTime()).isNull();

        // 送洗登記：待送洗 -> 清洗中。
        laundryService.start(laundry.getId(), "ZZ");
        assertThat(assetStatus()).isEqualTo(AssetLifecycleStatus.IN_LAUNDRY.getCode());

        // 完成清洗：清洗中 -> 庫存，清洗次數 +1。
        laundryService.complete(laundry.getId(), "ZZ");
        assertThat(assetStatus()).isEqualTo(AssetLifecycleStatus.AVAILABLE.getCode());
        assertThat(asset().getCleanCount()).isEqualTo(1);
        // 完成後不再出現在進行中列表。
        assertThat(laundryService.activePage("ZZ")).isEmpty();
    }

    @Test
    @DisplayName("完成清洗只累加一次清洗次數，重複完成被拒絕")
    void completeLaundryIncrementsCleanCountOnlyOnce() {
        issueService.create(issueRequest());
        returnService.create(returnLaunderRequest());
        LaundryRecordDO laundry = activeLaundry();
        laundryService.start(laundry.getId(), "ZZ");
        laundryService.complete(laundry.getId(), "ZZ");

        assertThat(asset().getCleanCount()).isEqualTo(1);
        // 清洗記錄已完成，重複完成應被拒絕且清洗次數不再增加。
        assertThatThrownBy(() -> laundryService.complete(laundry.getId(), "ZZ"))
                .isInstanceOf(BusinessException.class);
        assertThat(asset().getCleanCount()).isEqualTo(1);
    }

    private int assetStatus() {
        return asset().getLifecycleStatus();
    }

    private AssetDO asset() {
        return assetMapper.selectOne(new LambdaQueryWrapper<AssetDO>()
                .eq(AssetDO::getAssetCode, "J-Y-001")
                .eq(AssetDO::getSiteCode, "ZZ"));
    }

    private LaundryRecordDO activeLaundry() {
        return laundryRecordMapper.selectOne(new LambdaQueryWrapper<LaundryRecordDO>()
                .eq(LaundryRecordDO::getAssetCode, "J-Y-001")
                .eq(LaundryRecordDO::getSiteCode, "ZZ")
                .eq(LaundryRecordDO::getRecordStatus, BusinessRecordStatus.ACTIVE.getCode()));
    }

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

    private ReturnCreateReqVO returnLaunderRequest() {
        ReturnCreateReqVO request = new ReturnCreateReqVO();
        request.setSiteCode("ZZ");
        request.setAssetCode("J-Y-001");
        request.setDisposition(ReturnDisposition.LAUNDER.getCode());
        request.setReturnDate(LocalDate.of(2026, 8, 13));
        return request;
    }
}
