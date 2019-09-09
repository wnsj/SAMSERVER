package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.PayserviceBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PayserviceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 收费项目 前端控制器
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */

@RestController
@Scope("prototype")
@RequestMapping("/payserviceAction")
public class PayserviceAction {

    @Autowired
    private PayserviceService payserviceService;

    @PostMapping("/queryPayservice")
    public JSONObject queryPayservice(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PayserviceBean payserviceBean = JSONObject.parseObject(params, PayserviceBean.class);
        jsonObject.put(Constant.Result.RETDATA, payserviceService.queryPayservice(payserviceBean));
        return jsonObject;
    }

    @PostMapping("/addPayservice")
    public JSONObject addPayservice(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PayserviceBean payserviceBean = JSONObject.parseObject(params, PayserviceBean.class);
        payserviceService.addPayservice(payserviceBean);
        return jsonObject;
    }

    @PostMapping("/updatePayservice")
    public JSONObject updatePayservice(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PayserviceBean payserviceBean = JSONObject.parseObject(params, PayserviceBean.class);
        payserviceService.updatePayservice(payserviceBean);
        return jsonObject;
    }
}
