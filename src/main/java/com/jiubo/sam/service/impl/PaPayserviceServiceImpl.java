package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.bean.PaymentBean;
import com.jiubo.sam.dao.PaPayserviceDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PaPayserviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.util.CollectionsUtils;
import com.jiubo.sam.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
            paPayserviceBean.setPpId(paPayserviceBeans.get(0).getPpId());
        }else {
            String newDate = TimeUtil.getDateYYYY_MM_DD(TimeUtil.getDBTime());
            QueryWrapper<PaPayserviceBean> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.select("*");
            queryWrapper1.eq(true, "PATIENT_ID", paPayserviceBean.getPatientId());
            queryWrapper1.eq(true, "PAYSERVICE_ID", paPayserviceBean.getPayserviceId());
            queryWrapper1.eq(true, "BEG_DATE", newDate);
            queryWrapper1.eq(true, "IS_USE", '0');
            List<PaPayserviceBean> paPayserviceBeans1 = paPayserviceDao.selectList(queryWrapper1);
            if (paPayserviceBeans1.size()>0){
                paPayserviceDao.updatePaPayServiceById(paPayserviceBeans1.get(0));
                paPayserviceBean.setPpId(paPayserviceBeans1.get(0).getPpId());
            }else {
                if ("0".equals(paPayserviceBean.getIsUse())) throw new MessageException("请选择开始计时");
                paPayserviceDao.addPaPayService(paPayserviceBean);
            }
        }
        return paPayserviceBean;
    }

    @Override
    public List<PaPayserviceBean> getPaPayServiceByCon(PaPayserviceBean paPayserviceBean) {
        List<PaPayserviceBean> payserviceBeans = paPayserviceDao.getPaPayServiceByCon(paPayserviceBean);
        formatDate(payserviceBeans);
        return payserviceBeans;
    }

    @Override
    public Page<PaPayserviceBean> getPaPayServiceByPage(PaPayserviceBean paPayserviceBean) {
        Page<PaPayserviceBean> page = new Page<>(Long.parseLong(paPayserviceBean.getPageNum()),Long.parseLong(paPayserviceBean.getPageSize()));
        List<PaPayserviceBean> payserviceBeans = paPayserviceDao.getPaPayServiceByCon(page,paPayserviceBean);
        formatDate(payserviceBeans);
        return page.setRecords(payserviceBeans);
    }

    private void formatDate(List<PaPayserviceBean> payserviceBeans) {
        if (!CollectionsUtils.isEmpty(payserviceBeans)) {
            payserviceBeans.stream().peek(item -> {
                if (StringUtils.isNotBlank(item.getBegDate())) {
                    item.setBegDate(item.getBegDate().substring(0, 10));
                }
                if (StringUtils.isNotBlank(item.getEndDate())) {
                    item.setEndDate(item.getEndDate().substring(0, 10));
                }
            }).collect(Collectors.toList());
        }
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
