package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.LogRecordsBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 日志记录表 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2020-10-17
 */
public interface LogRecordsDao extends BaseMapper<LogRecordsBean> {

    //查询日志数据
    List<LogRecordsBean> queryLogRecords(LogRecordsBean logRecordsBean);
}
