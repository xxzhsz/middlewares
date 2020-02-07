package com.hx.middleware.model.mapper;


import com.hx.middleware.model.entity.RedRecord;

public interface RedRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RedRecord record);

    int insertSelective(RedRecord record);

    RedRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RedRecord record);

    int updateByPrimaryKey(RedRecord record);
}
