package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.PayserviceBean;
import com.jiubo.sam.dao.PayserviceDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PayserviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 收费项目服务实现类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Service
public class PayserviceServiceImpl extends ServiceImpl<PayserviceDao, PayserviceBean> implements PayserviceService {

    @Autowired
    private PayserviceDao payserviceDao;

    @Override
    public List<PayserviceBean> queryPayservice(PayserviceBean payserviceBean) throws MessageException {
        return payserviceDao.queryPayservice(payserviceBean);
    }

    @Override
    public void addPayservice(PayserviceBean payserviceBean) throws MessageException {
        if(StringUtils.isBlank(payserviceBean.getName()))throw new MessageException("项目名不能为空!");
        QueryWrapper<PayserviceBean> wrapper = new QueryWrapper<PayserviceBean>();
        wrapper.select("*");
        wrapper.eq(true, "NAME", payserviceBean.getName());
        if(payserviceDao.selectList(wrapper).size() > 0)throw new MessageException("该项目名已存在!");
        if(payserviceDao.insert(payserviceBean) <= 0)throw new MessageException("操作失败!");
    }

    @Override
    public void updatePayservice(PayserviceBean payserviceBean) throws MessageException {
        if(StringUtils.isBlank(payserviceBean.getName()))throw new MessageException("项目名不能为空!");
        QueryWrapper<PayserviceBean> wrapper = new QueryWrapper<PayserviceBean>();
        wrapper.select("*");
        wrapper.eq(true, "NAME", payserviceBean.getName());
        wrapper.ne("PAYSERVICE_ID", payserviceBean.getPayserviceId());
        if(payserviceDao.selectList(wrapper).size() > 0)throw new MessageException("该项目名已存在!");
       if(payserviceDao.updateById(payserviceBean) <= 0)throw new MessageException("操作失败!") ;
    }
}
