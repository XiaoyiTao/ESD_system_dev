package com.foxconn.iad.esd.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foxconn.iad.esd.dal.dataobject.AssetDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AssetMapper extends BaseMapper<AssetDO> {
    // 资产交易阶段会在此补充带版本条件的锁定/更新 SQL。
}
