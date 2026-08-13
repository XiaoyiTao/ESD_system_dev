package com.foxconn.iad.esd.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foxconn.iad.esd.dal.dataobject.AssetDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AssetMapper extends BaseMapper<AssetDO> {
    // 資產交易階段會在此補充帶版本條件的鎖定/更新 SQL。
}
