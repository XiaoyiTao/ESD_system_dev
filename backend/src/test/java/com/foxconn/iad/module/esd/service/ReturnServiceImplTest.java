package com.foxconn.iad.module.esd.service;

import com.foxconn.iad.module.esd.controller.admin.returns.vo.ReturnCreateReqVO;
import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.IssueRecordDO;
import com.foxconn.iad.module.esd.dal.dataobject.LaundryRecordDO;
import com.foxconn.iad.module.esd.dal.dataobject.ReturnRecordDO;
import com.foxconn.iad.module.esd.dal.mapper.AssetEventMapper;
import com.foxconn.iad.module.esd.dal.mapper.AssetMapper;
import com.foxconn.iad.module.esd.dal.mapper.IssueRecordMapper;
import com.foxconn.iad.module.esd.dal.mapper.LaundryRecordMapper;
import com.foxconn.iad.module.esd.dal.mapper.ReturnRecordMapper;
import com.foxconn.iad.module.esd.domain.AssetLifecycleStatus;
import com.foxconn.iad.module.esd.domain.BusinessRecordStatus;
import com.foxconn.iad.module.esd.domain.ReturnDisposition;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReturnServiceImplTest {

    @Mock
    private AssetMapper assetMapper;
    @Mock
    private ReturnRecordMapper returnRecordMapper;
    @Mock
    private IssueRecordMapper issueRecordMapper;
    @Mock
    private LaundryRecordMapper laundryRecordMapper;
    @Mock
    private AssetEventMapper assetEventMapper;
    @Mock
    private SiteAccessService siteAccessService;
    @InjectMocks
    private ReturnServiceImpl returnService;

    private final LoginUserContext operator = new LoginUserContext(1L, 1L, Set.of("ZZ"), false);

    @Test
    @DisplayName("回收直接入庫時結算發放記錄並回到庫存")
    void restockShouldSettleIssueRecordAndRestoreAvailable() {
        when(siteAccessService.requireSite("ZZ")).thenReturn(operator);
        when(assetMapper.selectOne(any())).thenReturn(issuedAsset());
        when(assetMapper.update(any(), any())).thenReturn(1);
        when(issueRecordMapper.selectOne(any())).thenReturn(activeIssue());

        returnService.create(request(ReturnDisposition.RESTOCK.getCode()));

        ArgumentCaptor<ReturnRecordDO> captor = ArgumentCaptor.forClass(ReturnRecordDO.class);
        verify(returnRecordMapper).insert(captor.capture());
        assertThat(captor.getValue().getDisposition()).isEqualTo(ReturnDisposition.RESTOCK.getCode());
        // 送洗處置不建立清洗記錄。
        verify(issueRecordMapper).updateById(any(IssueRecordDO.class));
    }

    @Test
    @DisplayName("回收送洗時進入待送洗並建立清洗記錄")
    void launderShouldEnterPendingLaundryAndCreateRecord() {
        when(siteAccessService.requireSite("ZZ")).thenReturn(operator);
        when(assetMapper.selectOne(any())).thenReturn(issuedAsset());
        when(assetMapper.update(any(), any())).thenReturn(1);
        when(issueRecordMapper.selectOne(any())).thenReturn(activeIssue());

        returnService.create(request(ReturnDisposition.LAUNDER.getCode()));

        ArgumentCaptor<LaundryRecordDO> captor = ArgumentCaptor.forClass(LaundryRecordDO.class);
        verify(laundryRecordMapper).insert(captor.capture());
        assertThat(captor.getValue().getRecordStatus()).isEqualTo(BusinessRecordStatus.ACTIVE.getCode());
        // 送洗時間待送洗登記時才寫入。
        assertThat(captor.getValue().getSendTime()).isNull();
    }

    @Test
    @DisplayName("並發回收時條件更新失敗應拋出衝突")
    void createShouldThrowConflictWhenAssetAlreadyReturned() {
        when(siteAccessService.requireSite("ZZ")).thenReturn(operator);
        when(assetMapper.selectOne(any())).thenReturn(issuedAsset());
        when(assetMapper.update(any(), any())).thenReturn(0);

        assertThatThrownBy(() -> returnService.create(request(ReturnDisposition.RESTOCK.getCode())))
                .isInstanceOf(BusinessException.class)
                .hasMessage("資產狀態已改變，回收失敗");
    }

    private AssetDO issuedAsset() {
        AssetDO asset = new AssetDO();
        asset.setId(100L);
        asset.setAssetCode("J-Y-001");
        asset.setSiteCode("ZZ");
        asset.setAssetType(1);
        asset.setLifecycleStatus(AssetLifecycleStatus.ISSUED.getCode());
        asset.setCurrentHolderUserId(200L);
        asset.setCurrentHolderNo("E001");
        asset.setCurrentHolderName("張三");
        asset.setVersion(5);
        return asset;
    }

    private IssueRecordDO activeIssue() {
        IssueRecordDO record = new IssueRecordDO();
        record.setId(50L);
        record.setAssetId(100L);
        record.setSiteCode("ZZ");
        record.setRecordStatus(BusinessRecordStatus.ACTIVE.getCode());
        return record;
    }

    private ReturnCreateReqVO request(int disposition) {
        ReturnCreateReqVO request = new ReturnCreateReqVO();
        request.setSiteCode("ZZ");
        request.setAssetCode("J-Y-001");
        request.setDisposition(disposition);
        request.setReturnDate(LocalDate.of(2026, 8, 13));
        return request;
    }
}
