package com.jiubo.sam.service;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.dto.NoMeDto;
import com.jiubo.sam.exception.MessageException;
import org.apache.ibatis.annotations.Param;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 交费 服务类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface PaymentService extends IService<PaymentBean> {

    //收费信息汇总查询
    public JSONObject queryGatherPayment(Map<String, Object> map) throws MessageException, Exception;

    //根据患者Id查询交费和欠费信息
    public List<PaymentBean> queryNewByPatientIdPayment(PaymentBean paymentBean) throws Exception;

    //收费明细信息
    public JSONObject queryPaymentList(Map<String, Object> map) throws MessageException, Exception;

    //根据患者Id查询交费信息
    public List<PaymentBean> queryPaymentByPatientId(String patientId);

    //查询患者的交费信息
    public List<PaymentBean> queryPaymentByHospNum(String hospNum, String patientId);

    //根据患者Id及缴费时间查询交费信息
    public List<PaymentBean> queryPaymentByPatientIdTime(Map<String, Object> map);

    //添加收费信息
    public void addPayment(List<PaymentBean> list) throws Exception;

    //修改交费信息
    public void updatePayment(List<PaymentBean> list) throws Exception;

    //删除缴费信息
    public void deletePayment(List<PaymentBean> list) throws Exception;

    //查询患者信息
    public JSONObject queryPatient(Map<String, Object> map) throws MessageException, ParseException;

    //根据患者Id和收费项目Id查询收费项目
    public PaymentBean queryPaymentByPatientIdPayserviceId(PaymentBean paymentBean) throws MessageException;

    public Map<String, Object> queryGatherPaymentListInfo(PatientBean patientBean) throws Exception;

    PayCount getPaymentDetails(PaymentBean paymentBean);

    public List<Map<String, Object>> queryPatientGatherDetails(PaymentBean paymentBean) throws Exception;

    public boolean jurdgePatientInDept(String hospNum,List<String> deptList) throws Exception;

    List<NoMeDto> getPayNoMe(JSONObject jsonObject) throws MessageException;

    List<PaymentOne> getPaymentOne(PaymentOneCondition condition);
}
