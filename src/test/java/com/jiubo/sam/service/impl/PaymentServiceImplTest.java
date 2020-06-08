package com.jiubo.sam.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PaymentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Struct;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @desc:
 * @date: 2019-09-12 16:04
 * @author: dx
 * @version: 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PaymentServiceImplTest {

    @Autowired
    private PaymentService paymentService;

    @Test
    public void queryGatherPayment() throws Exception {
        //JSONObject jsonObject = paymentService.queryGatherPayment(null);
        //JSONObject jsonObject = paymentService.queryPaymentList(null);
        //System.out.println(jsonObject.toJSONString());
        paymentService.queryPatient(null);
    }
}