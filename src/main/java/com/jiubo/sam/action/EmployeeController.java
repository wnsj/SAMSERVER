package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.EmployeeBean;
import com.jiubo.sam.bean.PositionBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.EmployeeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author syl
 * @since 2020-08-06
 */
@RestController
@RequestMapping("/employeeBean")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/getEmpByCondition")
    public JSONObject getEmpByCondition(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        EmployeeBean employeeBean = JSONObject.parseObject(params, EmployeeBean.class);
        jsonObject.put(Constant.Result.RETDATA, employeeService.getEmpByCondition(employeeBean));
        return jsonObject;
    }

    @PostMapping("/addEmp")
    public JSONObject addEmp(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        EmployeeBean employeeBean = JSONObject.parseObject(params, EmployeeBean.class);
        employeeService.addEmp(employeeBean);
        return jsonObject;
    }

    @PostMapping("/patchEmp")
    public JSONObject patchEmp(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        EmployeeBean employeeBean = JSONObject.parseObject(params, EmployeeBean.class);
        employeeService.patchEmp(employeeBean);
        return jsonObject;
    }
}
