package com.foxconn.iad.esd.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.foxconn.iad.esd.dal.dataobject.PersonProfileDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonProfileMapper extends BaseMapper<PersonProfileDO> {
}

