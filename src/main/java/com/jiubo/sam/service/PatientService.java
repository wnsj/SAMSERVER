package com.jiubo.sam.service;

import com.jiubo.sam.bean.PatientBean;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.exception.MessageException;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 患者基础信息 服务类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface PatientService extends IService<PatientBean> {

    //根据住院号查询患者信息
    public void queryPatientByHospNum(PatientBean patientBean) throws MessageException;

    //查询患者信息

    //添加病患信息
    public void addPatient(PatientBean patientBean) throws MessageException;

    //根据患者Id查询其收费项目（没有则返回所有收费项目）
    //public List queryPatientPayServiceById(PatientBean patientBean)throws MessageException;

    //保存患者基本信息
    public void addPatientList(Map<Object,Object> map) throws ParseException, Exception;

}
