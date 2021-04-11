package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.bean.PatinetMarginBean;
import com.jiubo.sam.bean.PaymentDetailsBean;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.dao.PatinetMarginDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.LogRecordsService;
import com.jiubo.sam.service.PatinetMarginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PatinetMarginServiceImpl implements PatinetMarginService {

    @Autowired
    private LogRecordsService logRecordsService;

    @Autowired
    private PatinetMarginDao patinetMarginDao;

    @Autowired
    private PatientDao patientDao;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addPatinetMargin(PatinetMarginBean patinetMarginBean) throws Exception{
        //查询此患者信息
        QueryWrapper<PatientBean> patientBeanQueryWrapper = new QueryWrapper<>();
        patientBeanQueryWrapper.eq("HOSP_NUM",patinetMarginBean.getHospNum());
        List<PatientBean> patientBeanList = patientDao.selectList(patientBeanQueryWrapper);
        if(CollectionUtils.isEmpty(patientBeanList)){
            throw new MessageException("患者数据异常");
        }
        PatientBean patientBean = patientBeanList.get(0);

        //对于住院和门诊的基础缴费信息设置
        PaymentDetailsBean paymentDetailsBean = new PaymentDetailsBean();
        paymentDetailsBean.setType(3)
                .setHospNum(patientBean.getHospNum())
                .setCreateDate(LocalDateTime.now())
                .setDeptId(Integer.valueOf(patientBean.getDeptId()))
                .setIsInHospital(Integer.valueOf(patientBean.getInHosp()))
                .setRemarks(patinetMarginBean.getRemark())
                .setEmpId(Integer.valueOf(patientBean.getEmpId()))
                .setMarginUse(patinetMarginBean.getMoney());

        //查询此患者是否交过押金
        patinetMarginBean.setCreateDate(LocalDateTime.now());
        QueryWrapper<PatinetMarginBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("HOSP_NUM",patinetMarginBean.getHospNum());
        List<PatinetMarginBean> list = patinetMarginDao.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            //设置缴费记录里是添加还是退费
            paymentDetailsBean.setMarginType(1);
            if (patinetMarginDao.insert(patinetMarginBean) <= 0) {
                throw new MessageException("操作失败!");
            }
        }else {
            PatinetMarginBean entity = list.get(0);
            if(patinetMarginBean.getType().equals(1)){
                //设置缴费记录里是添加还是退费
                paymentDetailsBean.setMarginType(1);
                entity.setModifyDate(LocalDateTime.now());
                entity.setMoney(entity.getMoney()+patinetMarginBean.getMoney());
            }else {
                //设置缴费记录里是添加还是退费
                paymentDetailsBean.setMarginType(2);
                entity.setModifyDate(LocalDateTime.now());
                entity.setMoney(entity.getMoney()-patinetMarginBean.getMoney());
            }
            patinetMarginDao.updateById(entity);
        }
    }
}
