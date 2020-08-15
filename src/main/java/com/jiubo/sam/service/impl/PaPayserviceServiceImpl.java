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
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    public void addAndUpdatePaPayService(List<PaPayserviceBean> list) throws Exception {
        if (list == null || list.isEmpty()) return;
        paPayserviceDao.addAndUpdatePaPayService(list);
    }

    @Override
    public PaPayserviceBean addAndUpdatePps(PaPayserviceBean paPayserviceBean) throws Exception {
        QueryWrapper<PaPayserviceBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "HOSP_NUM", paPayserviceBean.getHospNum());
        queryWrapper.eq(true, "PAYSERVICE_ID", paPayserviceBean.getPayserviceId());
        queryWrapper.eq(true, "IS_USE", '1');
        List<PaPayserviceBean> paPayserviceBeans = paPayserviceDao.selectList(queryWrapper);
        if (paPayserviceBeans.size()>0){
            paPayserviceDao.updatePaPayService(paPayserviceBean.setPpId(paPayserviceBeans.get(0).getPpId()));
        }else {
            if ("0".equals(paPayserviceBean.getIsUse())) throw new MessageException("请选择开始计时");
            paPayserviceDao.addPaPayService(paPayserviceBean);
        }
        return paPayserviceBean;
    }

    @Override
    public void updatePaPayService(PaPayserviceBean paPayserviceBean) throws Exception {
        QueryWrapper<PaPayserviceBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "PP_ID", paPayserviceBean.getPpId());
        List<PaPayserviceBean> paPayserviceBeans = paPayserviceDao.selectList(queryWrapper);
        if (paPayserviceBeans.size()<=0){
            throw new MessageException("没有开启无法修改");
        }else {
            paPayserviceDao.updatePaPayService(paPayserviceBean);
        }
    }
}
