package com.foxconn.iad.module.esd.service;

import com.foxconn.iad.module.esd.dal.dataobject.AssetDO;
import com.foxconn.iad.module.esd.dal.dataobject.LaundryRecordDO;
import com.foxconn.iad.module.esd.dal.mysql.AssetEventMapper;
import com.foxconn.iad.module.esd.dal.mysql.AssetMapper;
import com.foxconn.iad.module.esd.dal.mysql.LaundryRecordMapper;
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

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LaundryServiceImplTest {

    @Mock
    private AssetMapper assetMapper;
    @Mock
    private LaundryRecordMapper laundryRecordMapper;
    @Mock
    private AssetEventMapper assetEventMapper;
    @Mock
    private SiteAccessService siteAccessService;
    @InjectMocks
    private LaundryServiceImpl laundryService;

    private final LoginUserContext operator = new LoginUserContext(1L, 1L, Collections.singleton("ZZ"), false);

    @Test
    @DisplayName("送洗登記時待送洗推進為清洗中並寫入送洗時間")
    void startShouldAdvanceToInLaundryAndRecordSendTime() {
        when(siteAccessService.requireSite("ZZ")).thenReturn(operator);
        when(laundryRecordMapper.selectOne(any())).thenReturn(activeLaundry());
        when(assetMapper.selectOne(any())).thenReturn(asset(AssetLifecycleStatus.PENDING_LAUNDRY, 2));
        when(assetMapper.update(any(), any())).thenReturn(1);

        laundryService.start(10L, "ZZ");

        ArgumentCaptor<LaundryRecordDO> captor = ArgumentCaptor.forClass(LaundryRecordDO.class);
        verify(laundryRecordMapper).updateById(captor.capture());
        assertThat(captor.getValue().getSendTime()).isNotNull();
    }

    @Test
    @DisplayName("完成清洗時回到庫存並累加清洗次數")
    void completeShouldRestoreAvailableAndIncrementCleanCount() {
        when(siteAccessService.requireSite("ZZ")).thenReturn(operator);
        when(laundryRecordMapper.selectOne(any())).thenReturn(activeLaundry());
        when(assetMapper.selectOne(any())).thenReturn(asset(AssetLifecycleStatus.IN_LAUNDRY, 2));
        when(assetMapper.update(any(), any())).thenReturn(1);

        laundryService.complete(10L, "ZZ");

        ArgumentCaptor<AssetDO> captor = ArgumentCaptor.forClass(AssetDO.class);
        verify(assetMapper).update(captor.capture(), any());
        assertThat(captor.getValue().getLifecycleStatus()).isEqualTo(AssetLifecycleStatus.AVAILABLE.getCode());
        // 清洗次數在原有 2 的基礎上加一。
        assertThat(captor.getValue().getCleanCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("完成清洗時條件更新失敗應拋出衝突")
    void completeShouldThrowConflictWhenConditionalUpdateFails() {
        when(siteAccessService.requireSite("ZZ")).thenReturn(operator);
        when(laundryRecordMapper.selectOne(any())).thenReturn(activeLaundry());
        when(assetMapper.selectOne(any())).thenReturn(asset(AssetLifecycleStatus.IN_LAUNDRY, 2));
        when(assetMapper.update(any(), any())).thenReturn(0);

        assertThatThrownBy(() -> laundryService.complete(10L, "ZZ"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("資產狀態已改變，完成清洗失敗");
    }

    private LaundryRecordDO activeLaundry() {
        LaundryRecordDO record = new LaundryRecordDO();
        record.setId(10L);
        record.setSiteCode("ZZ");
        record.setAssetId(100L);
        record.setAssetCode("J-Y-001");
        record.setRecordStatus(BusinessRecordStatus.ACTIVE.getCode());
        return record;
    }

    private AssetDO asset(AssetLifecycleStatus status, int cleanCount) {
        AssetDO asset = new AssetDO();
        asset.setId(100L);
        asset.setAssetCode("J-Y-001");
        asset.setSiteCode("ZZ");
        asset.setAssetType(1);
        asset.setLifecycleStatus(status.getCode());
        asset.setCleanCount(cleanCount);
        asset.setVersion(4);
        return asset;
    }
}
