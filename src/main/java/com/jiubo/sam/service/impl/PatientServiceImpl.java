package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.bean.PaymentBean;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.service.PaymentService;
import com.jiubo.sam.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 患者基础信息 服务实现类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientDao, PatientBean> implements PatientService {

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private PaymentService paymentService;

    @Override
    public void queryPatientByHospNum(PatientBean patientBean) throws MessageException {
        QueryWrapper<PatientBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(true, "HOSP_NUM", patientBean.getHospNum());
        //queryWrapper.select("PATIENT_ID", "HOSP_NUM", "NAME");
        queryWrapper.select("*");
        List<PatientBean> patientBeans = patientDao.selectList(queryWrapper);
        if (patientBeans.size() > 0) throw new MessageException("住院号为" + patientBean.getHospNum() + "患者已存在!");
    }

    @Override
    @Transactional
    public void addPatient(PatientBean patientBean) throws MessageException {
        //查询患者信息
        queryPatientByHospNum(patientBean);

        String nowStr = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime());
        patientBean.setHospTime(nowStr);
        patientBean.setUpdateTime(nowStr);
        //插入患者信息
        patientDao.addPatient(patientBean);

        if (patientBean.getPaymentList() != null && patientBean.getPaymentList().size() > 0) {
            for (PaymentBean paymentBean : patientBean.getPaymentList()) {
                paymentBean.setPatientId(patientBean.getPatientId());
                paymentBean.setUpdatetime(nowStr);
                //插入交费信息
                paymentService.addPayment(paymentBean);
                //插入患者、收费项目、交费记录关系表
                patientDao.addPatientPayservicePayment(patientBean.getPatientId(), paymentBean.getPayserviceId(), paymentBean.getPaymentId());
            }
        }
    }

    @Override
    public void addPatientList(Map<Object, Object> map) {
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "##" + entry.getValue());
        }
    }
}
