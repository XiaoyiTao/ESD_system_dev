package com.foxconn.iad.module.esd.dal.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foxconn.iad.module.esd.dal.dataobject.PersonProfileDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonProfileMapper extends BaseMapper<PersonProfileDO> {
    // 當前查詢全部使用 MyBatis-Plus 基礎 CRUD；複雜交易 SQL 在後續版本按業務單獨擴展。
}
