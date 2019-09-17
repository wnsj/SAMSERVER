package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.PaymentBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PaymentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  交费 前端控制器
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@RestController
@Scope("prototype")
@RequestMapping("/paymentAction")
public class PaymentAction {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/queryGatherPayment")
    public JSONObject queryGatherPayment(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        Map<String,Object> map = JSONObject.parseObject(params, Map.class);
        jsonObject.put(Constant.Result.RETDATA,paymentService.queryGatherPayment(map) );
        return jsonObject;
    }

    //添加或修改交费信息[{paymentId:0,patientId:1, payserviceId:1, receivable:2000, actualpayment:2000, begtime:'2019-01-01', endtime:'2019-01-31', paymenttime:'2019-01-01', isuse:'true' }]
    @PostMapping("/addUpdatePayment")
    public JSONObject addUpdatePayment(@RequestBody String params) throws Exception {

        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        List<PaymentBean> list = JSONArray.parseArray(params,PaymentBean.class);
        paymentService.addUpdatePayment(list);
        return jsonObject;
    }


    @PostMapping("/queryPayment")
    public JSONObject queryPayment(@RequestBody String params) throws Exception {

        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        Map<String,Object> map = JSONObject.parseObject(params, Map.class);
        paymentService.queryPayment(map);
        return jsonObject;
    }
}
