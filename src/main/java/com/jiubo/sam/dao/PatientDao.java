package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PatientBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 患者基础信息 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface PatientDao extends BaseMapper<PatientBean> {

    //添加患者信息
    public int addPatient(PatientBean patientBean);

    //插入患者、收费项目、交费记录关系
    public int addPatientPayservicePayment(String patientId,String payserviceId,String paymentId);
}
