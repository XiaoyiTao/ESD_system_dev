package com.foxconn.iad.esd.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foxconn.iad.esd.dal.dataobject.PersonProfileDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonProfileMapper extends BaseMapper<PersonProfileDO> {
    // 当前查询全部使用 MyBatis-Plus 基础 CRUD；复杂交易 SQL 在后续版本按业务单独扩展。
}
