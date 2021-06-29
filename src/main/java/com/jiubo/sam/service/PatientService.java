package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.PatientBean;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.bean.PaymentBean;
import com.jiubo.sam.bean.PayserviceBean;
import com.jiubo.sam.dto.ConfirmClosedDto;
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
    public PatientBean queryPatientByHospNum(PatientBean patientBean) throws MessageException;

    //查询患者信息
    public Page<PatientBean> queryPatient(String page, String pageSize, PatientBean patientBean) throws Exception;

    //患者精确查询
    public PatientBean accurateQuery(PatientBean patientBean);

    //患者模糊查询
    public PatientBean fuzzyQuery(PatientBean patientBean) throws Exception;

    //根据患者Id及收费时间查询患者信息及缴费信息
    PatientBean queryPatientPaymentByIdTime(Map<String, Object> map) throws MessageException;

    //添加病患信息
    public PatientBean addPatient(PatientBean patientBean) throws Exception;

    //根据患者Id查询其收费项目（没有则返回所有收费项目）
    //public List queryPatientPayServiceById(PatientBean patientBean)throws MessageException;

    //保存患者基本信息
    public void addPatientList(Map<Object, Object> map, String accountId) throws ParseException, Exception;


    //保存患者基本信息
    public List<PatientBean> queryPatientListByHospNum(Map<Object, Object> map, String accountId) throws ParseException, Exception;

    //新的收费信息汇总查询
    public List<PatientBean> queryGatherNewPayment(PatientBean patientBean) throws MessageException, Exception;


    //根据部门添加缴费启动项
    public void startUpPayService(PatientBean patientBean) throws Exception;

    //获取医疗费和非医疗的欠费情况
    public Map<String,Object> patientArrears(PatientBean patientBean) throws Exception;

    //根据住院号修改维护医生
    void updateDoctorByHospNum(PatientBean patientBean);

    Boolean confirmClosed(ConfirmClosedDto confirmClosedDto) throws MessageException;

    void lose(ConfirmClosedDto confirmClosedDto) throws MessageException;

    PayserviceBean selectIsUse(Integer i);
}
