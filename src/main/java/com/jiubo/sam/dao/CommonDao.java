package com.jiubo.sam.dao;

import org.apache.ibatis.annotations.Select;

import java.util.Date;

/**
 * @desc:
 * @date: 2019-09-07 13:40
 * @author: dx
 * @version: 1.0
 */
public interface CommonDao {
    //获取数据库时间
    @Select("SELECT GETDATE()")
    public Date getDBTime();
}
