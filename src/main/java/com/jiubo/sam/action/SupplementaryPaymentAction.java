package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.SupplementaryPaymentBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.SupplementaryPaymentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 医疗费补缴前端控制器
 * </p>
 *
 * @author dx
 * @since 2020-08-11
 */
@RestController
@RequestMapping("/supplementaryPaymentAction")
public class SupplementaryPaymentAction {

    @Autowired
    private SupplementaryPaymentService supplementaryPaymentService;

    //添加医疗费补缴记录
    @PostMapping("/addSupplementaryPayment")
    public JSONObject addSupplementaryPayment(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        SupplementaryPaymentBean supplementaryPaymentBean = JSONObject.parseObject(params, SupplementaryPaymentBean.class);
        jsonObject.put(Constant.Result.RETDATA, supplementaryPaymentService.addSupplementaryPayment(supplementaryPaymentBean));
        return jsonObject;
    }

    //修改医疗费补缴记录
    @PostMapping("/updateSupplementaryPayment")
    public JSONObject updateSupplementaryPayment(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        SupplementaryPaymentBean supplementaryPaymentBean = JSONObject.parseObject(params, SupplementaryPaymentBean.class);
        jsonObject.put(Constant.Result.RETDATA, supplementaryPaymentService.updateSupplementaryPayment(supplementaryPaymentBean));
        return jsonObject;
    }

    //查询补缴记录
    @PostMapping("/querySupplementaryPayment")
    public JSONObject querySupplementaryPayment(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        SupplementaryPaymentBean supplementaryPaymentBean = JSONObject.parseObject(params, SupplementaryPaymentBean.class);
        jsonObject.put(Constant.Result.RETDATA, supplementaryPaymentService.querySupplementaryPayment(supplementaryPaymentBean));
        return jsonObject;
    }
}
