package com.foxconn.iad.module.esd.service;

import com.foxconn.iad.module.esd.controller.admin.issue.vo.IssueCreateReqVO;
import com.foxconn.iad.module.esd.controller.admin.person.vo.PersonProfileRespVO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.IssueRecordDO;
import com.foxconn.iad.module.esd.dal.mysql.AssetEventMapper;
import com.foxconn.iad.module.esd.dal.mysql.AssetMapper;
import com.foxconn.iad.module.esd.dal.mysql.IssueRecordMapper;
import com.foxconn.iad.module.esd.enums.AssetLifecycleStatus;
import com.foxconn.iad.module.esd.enums.BusinessRecordStatus;
import com.foxconn.iad.module.esd.exception.BusinessException;
import com.foxconn.iad.module.esd.security.LoginUserContext;
import com.foxconn.iad.module.esd.security.SiteAccessService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IssueServiceImplTest {

    @Mock
    private AssetMapper assetMapper;
    @Mock
    private IssueRecordMapper issueRecordMapper;
    @Mock
    private AssetEventMapper assetEventMapper;
    @Mock
    private PersonProfileService personProfileService;
    @Mock
    private SiteAccessService siteAccessService;
    @InjectMocks
    private IssueServiceImpl issueService;

    private final LoginUserContext operator = new LoginUserContext(1L, 1L, Collections.singleton("ZZ"), false);

    @Test
    @DisplayName("發放成功時寫入發放記錄並快照員工信息")
    void createShouldWriteRecordAndSnapshotEmployee() {
        when(siteAccessService.requireSite("ZZ")).thenReturn(operator);
        when(personProfileService.requireEnabledByUser("ZZ", 200L)).thenReturn(person());
        when(assetMapper.selectOne(any())).thenReturn(availableAsset());
        when(assetMapper.update(any(), any())).thenReturn(1);

        issueService.create(request());

        ArgumentCaptor<IssueRecordDO> captor = ArgumentCaptor.forClass(IssueRecordDO.class);
        verify(issueRecordMapper).insert(captor.capture());
        IssueRecordDO record = captor.getValue();
        assertThat(record.getAssetCode()).isEqualTo("J-Y-001");
        assertThat(record.getEmployeeNo()).isEqualTo("E001");
        assertThat(record.getEmployeeName()).isEqualTo("張三");
        assertThat(record.getSupervisorName()).isEqualTo("李四");
        assertThat(record.getDeptName()).isEqualTo("一課");
        assertThat(record.getFloorCode()).isEqualTo("2F");
        assertThat(record.getRecordStatus()).isEqualTo(BusinessRecordStatus.ACTIVE.getCode());
    }

    @Test
    @DisplayName("並發發放同一資產時條件更新失敗應拋出衝突")
    void createShouldThrowConflictWhenAssetAlreadyTaken() {
        when(siteAccessService.requireSite("ZZ")).thenReturn(operator);
        when(personProfileService.requireEnabledByUser("ZZ", 200L)).thenReturn(person());
        when(assetMapper.selectOne(any())).thenReturn(availableAsset());
        // 條件更新影響行數為 0，表示資產已被其他請求搶佔。
        when(assetMapper.update(any(), any())).thenReturn(0);

        assertThatThrownBy(() -> issueService.create(request()))
                .isInstanceOf(BusinessException.class)
                .hasMessage("資產已被其他操作更新，發放失敗");
    }

    private AssetDO availableAsset() {
        AssetDO asset = new AssetDO();
        asset.setId(100L);
        asset.setAssetCode("J-Y-001");
        asset.setSiteCode("ZZ");
        asset.setAssetType(1);
        asset.setLifecycleStatus(AssetLifecycleStatus.AVAILABLE.getCode());
        asset.setCleanCount(0);
        asset.setVersion(3);
        return asset;
    }

    private PersonProfileRespVO person() {
        PersonProfileRespVO person = new PersonProfileRespVO();
        person.setPlatformUserId(200L);
        person.setEmployeeNo("E001");
        person.setEmployeeName("張三");
        person.setSupervisorName("李四");
        person.setDeptName("一課");
        person.setFloorCode("2F");
        return person;
    }

    private IssueCreateReqVO request() {
        IssueCreateReqVO request = new IssueCreateReqVO();
        request.setSiteCode("ZZ");
        request.setEmployeeUserId(200L);
        request.setAssetCode("J-Y-001");
        request.setIssueDate(LocalDate.of(2026, 8, 13));
        return request;
    }
}
