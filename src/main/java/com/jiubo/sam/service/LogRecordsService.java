package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.LogRecordsBean;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.dao.LogRecordsDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * <p>
 * 日志记录表 服务类
 * </p>
 *
 * @author dx
 * @since 2020-10-17
 */
public interface LogRecordsService extends IService<LogRecordsBean> {

    //查询日志数据
    public PageInfo<LogRecordsBean> queryLogRecords(String page, String pageSize, LogRecordsBean logRecordsBean) throws Exception;

    //查询日志数据
    void insertLogRecords(LogRecordsBean logRecordsBean) throws Exception;
}
