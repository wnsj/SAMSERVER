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
 * 交费 前端控制器
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

    //收费信息汇总查询
    @PostMapping("/queryGatherPayment")
    public JSONObject queryGatherPayment(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        Map<String, Object> map = JSONObject.parseObject(params, Map.class);
        jsonObject.put(Constant.Result.RETDATA, paymentService.queryGatherPayment(map));
        return jsonObject;
    }

    /**
     * 新增，查询患者ID查询
     * @param params
     * @return
     * @throws Exception
     * author mwl
     * 2019-04-15
     */
    @PostMapping("/queryGatherNewPaymentInfo")
    public JSONObject queryGatherNewPayment(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PaymentBean paymentBean = JSONObject.parseObject(params, PaymentBean.class);
        jsonObject.put(Constant.Result.RETDATA, paymentService.queryNewByPatientIdPayment(paymentBean));
        return jsonObject;
    }

    //收费明细信息
    @PostMapping("/queryPaymentList")
    public JSONObject queryPaymentList(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        Map<String, Object> map = JSONObject.parseObject(params, Map.class);
        jsonObject.put(Constant.Result.RETDATA, paymentService.queryPaymentList(map));
        return jsonObject;
    }

    //添加交费信息[{paymentId:0,patientId:1, payserviceId:1, receivable:2000, actualpayment:2000, begtime:'2019-01-01', endtime:'2019-01-31', paymenttime:'2019-01-01', isuse:'true' }]
    @PostMapping("/addPayment")
    public JSONObject addUpdatePayment(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        List<PaymentBean> list = JSONArray.parseArray(params, PaymentBean.class);
        paymentService.addPayment(list);
        return jsonObject;
    }

    //修改交费信息[{paymentId:0,patientId:1, payserviceId:1, receivable:2000, actualpayment:2000, begtime:'2019-01-01', endtime:'2019-01-31', paymenttime:'2019-01-01', isuse:'true' }]
    @PostMapping("/updatePayment")
    public JSONObject updatePayment(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        List<PaymentBean> list = JSONArray.parseArray(params, PaymentBean.class);
        paymentService.updatePayment(list);
        return jsonObject;
    }

    //删除缴费信息
    @PostMapping("/deletePayment")
    public JSONObject deletePayment(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        List<PaymentBean> list = JSONArray.parseArray(params, PaymentBean.class);
        paymentService.deletePayment(list);
        return jsonObject;
    }

    //查询患者信息
    @PostMapping("/queryPatient")
    //{name:'',deptId:'',hospNum:'',sex:'',hospTime:'',outHosp:''}
    public JSONObject queryPatient(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        Map<String, Object> map = JSONObject.parseObject(params, Map.class);
        jsonObject.put(Constant.Result.RETDATA, paymentService.queryPatient(map));
        return jsonObject;
    }

    //根据患者Id和收费项目Id查询收费项目
    @PostMapping("/queryPaymentByPatientIdPayserviceId")
    //127.0.0.1:8080/paymentAction/queryPaymentByPatientIdPayserviceId?patientId=22&payserviceId=23
    public JSONObject queryPaymentByPatientIdPayserviceId(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PaymentBean paymentBean = JSONObject.parseObject(params, PaymentBean.class);
        jsonObject.put(Constant.Result.RETDATA, paymentService.queryPaymentByPatientIdPayserviceId(paymentBean));
        return jsonObject;
    }
}
