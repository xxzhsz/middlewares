package com.hx.middleware.model.mapper;


import com.hx.middleware.model.entity.UserAccountRecord;

public interface UserAccountRecordMapper {
    //插入记录
    int insert(UserAccountRecord record);
    //根据主键id查询
    UserAccountRecord selectByPrimaryKey(Integer id);
}
