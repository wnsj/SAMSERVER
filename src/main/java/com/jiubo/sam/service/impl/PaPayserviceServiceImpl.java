package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.bean.PaymentBean;
import com.jiubo.sam.dao.PaPayserviceDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PaPayserviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mwl
 * @since 2020-08-10
 */
@Service
public class PaPayserviceServiceImpl extends ServiceImpl<PaPayserviceDao, PaPayserviceBean> implements PaPayserviceService {

    @Autowired
    PaPayserviceDao paPayserviceDao;

    @Override
    public List<PaPayserviceBean> queryPaPayService(PaPayserviceBean paPayserviceBean) throws Exception {
        return paPayserviceDao.queryPaPayService(paPayserviceBean);
    }

    @Override
    public PaPayserviceBean addPaPayService(PaPayserviceBean paPayserviceBean) throws Exception {
        QueryWrapper<PaPayserviceBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "HOSP_NUM", paPayserviceBean.getHospNum());
        queryWrapper.eq(true, "PAYSERVICE_ID", paPayserviceBean.getPayserviceId());
        queryWrapper.eq(true, "IS_USE", paPayserviceBean.getIsUse());
        List<PaPayserviceBean> paPayserviceBeans = paPayserviceDao.selectList(queryWrapper);
        if (paPayserviceBeans.size()>0){
            throw new MessageException("已经填加,无需重复添加");
        }
        if (paPayserviceDao.addPaPayService(paPayserviceBean)>0){
            return paPayserviceBean;
        }else {
            throw new MessageException("数据插入失败");
        }
    }


    @Override
    public void addAndUpdatePaPayService(List<PaPayserviceBean> list) throws Exception {
        if (list == null || list.isEmpty()) return;
        paPayserviceDao.addAndUpdatePaPayService(list);
    }

    @Override
    public PaPayserviceBean updatePaPayService(PaPayserviceBean paPayserviceBean) throws Exception {
        QueryWrapper<PaPayserviceBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "HOSP_NUM", paPayserviceBean.getHospNum());
        queryWrapper.eq(true, "PAYSERVICE_ID", paPayserviceBean.getPayserviceId());
        queryWrapper.eq(true, "IS_USE", '1');
        List<PaPayserviceBean> paPayserviceBeans = paPayserviceDao.selectList(queryWrapper);
        if (paPayserviceBeans.size()<=0){
            throw new MessageException("没有开启无法修改");
        }else {

            return paPayserviceBean;
        }
    }
}
