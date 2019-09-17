package com.jiubo.sam.service;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.PaymentBean;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.exception.MessageException;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  交费 服务类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface PaymentService extends IService<PaymentBean> {

    //收费信息汇总查询
    public JSONObject queryGatherPayment(Map<String,Object> map) throws MessageException, Exception;

    //根据患者查询交费信息
    public List<PaymentBean> queryPaymentByPatientId(String patientId);


    //添加收费信息
    void addPayment(List<PaymentBean> list) throws MessageException;

    //修改交费信息
    void updatePayment(List<PaymentBean> list) throws MessageException;

}
