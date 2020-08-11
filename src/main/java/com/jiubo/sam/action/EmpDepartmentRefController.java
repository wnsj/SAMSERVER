package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.EmpDepartmentRefBean;
import com.jiubo.sam.bean.EmployeeBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.EmpDepartmentRefService;
import com.jiubo.sam.service.EmployeeService;
import com.jiubo.sam.service.impl.EmployeeServiceImpl;
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
 * @since 2020-08-10
 */
@RestController
@RequestMapping("/empDepartmentRefBean")
public class EmpDepartmentRefController {

    @Autowired
    private EmpDepartmentRefService empDepartmentRefService;

    @PostMapping("/getEdRefByEmpId")
    public JSONObject getEdRefByEmpId(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        EmpDepartmentRefBean empDepartmentRefBean = JSONObject.parseObject(params, EmpDepartmentRefBean.class);
        jsonObject.put(Constant.Result.RETDATA, empDepartmentRefService.getEdRefByEmpId(empDepartmentRefBean));
        return jsonObject;
    }
}
