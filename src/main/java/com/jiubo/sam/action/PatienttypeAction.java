package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.PatienttypeBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PatienttypeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 患者类型前端控制器
 * </p>
 *
 * @author dx
 * @since 2019-09-24
 */
@RestController
@Scope("prototype")
@RequestMapping("/patienttypeAction")
public class PatienttypeAction {

    @Autowired
    private PatienttypeService patienttypeService;

    //查询患者类型
    @PostMapping("/queryPatientType")
    public JSONObject queryPatientType(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatienttypeBean patienttypeBean = JSONObject.parseObject(params, PatienttypeBean.class);
        jsonObject.put(Constant.Result.RETDATA, patienttypeService.queryPatientType(patienttypeBean));
        return jsonObject;
    }

    //添加患者类型
    @PostMapping("/addPatientType")
    public JSONObject addPatientType(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatienttypeBean patienttypeBean = JSONObject.parseObject(params, PatienttypeBean.class);
        patienttypeService.addPatientType(patienttypeBean);
        return jsonObject;
    }

    //修改患者类型
    @PostMapping("/updatePatientType")
    public JSONObject updatePatientType(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatienttypeBean patienttypeBean = JSONObject.parseObject(params, PatienttypeBean.class);
        patienttypeService.updatePatientType(patienttypeBean);
        return jsonObject;
    }
}
