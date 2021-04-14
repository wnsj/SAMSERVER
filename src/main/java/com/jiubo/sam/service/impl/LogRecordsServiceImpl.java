package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.LogRecordsBean;
import com.jiubo.sam.dao.LogRecordsDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.LogRecordsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 日志记录表 服务实现类
 * </p>
 *
 * @author dx
 * @since 2020-10-17
 */
@Service
public class LogRecordsServiceImpl extends ServiceImpl<LogRecordsDao, LogRecordsBean> implements LogRecordsService {

    @Autowired
    private LogRecordsDao logRecordsDao;


    @Override
    public PageInfo<LogRecordsBean> queryLogRecords(String page, String pageSize, LogRecordsBean logRecordsBean) throws Exception {
        if (StringUtils.isBlank(page)) {
            page = "1";
        }
        if (StringUtils.isBlank(pageSize)) {
            pageSize = "10";
        }
        PageHelper.startPage(Integer.valueOf(page),Integer.valueOf(pageSize));
        List<LogRecordsBean> list = logRecordsDao.queryLogRecords(logRecordsBean);
        PageInfo<LogRecordsBean> result = new PageInfo<>(list);
        return result;
    }

    @Override
    public void insertLogRecords(LogRecordsBean logRecordsBean) throws Exception {
        if (logRecordsBean.getOperateId()<=0) throw new MessageException("操作人不能为空");
        if (StringUtils.isEmpty(logRecordsBean.getHospNum())) throw new MessageException("患者不能为空");

        int k =logRecordsDao.insert(logRecordsBean);
        if (k<0)throw new MessageException("日志插入失败");
    }
}
