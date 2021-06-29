package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.DepartmentBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.dto.UpdateDepartmentByIdsDto;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.DepartmentService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 科室前端控制器
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@RestController
@Scope("prototype")
@RequestMapping("/departmentAction")
public class DepartmentAction {

    @Autowired
    private DepartmentService departmentService;

    /* *
     * @desc:查询科室
     * @author: dx
     * @date: 2019-09-09 10:01:54
     * @param params :
     * @return: com.alibaba.fastjson.JSONObject
     * @throws:
     * @version: 1.0
     **/
    @PostMapping("/queryDepartment")
    public JSONObject queryDepartment(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        DepartmentBean departmentBean = JSONObject.parseObject(params, DepartmentBean.class);
        jsonObject.put(Constant.Result.RETDATA, departmentService.queryDepartment(departmentBean));
        return jsonObject;
    }

    /* *
     * @desc:添加科室
     * @author: dx
     * @date: 2019-09-09 10:02:14
     * @param params :
     * @return: com.alibaba.fastjson.JSONObject
     * @throws:
     * @version: 1.0
     **/
    @PostMapping("/addDepartment")
    public JSONObject addDepartment(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        DepartmentBean departmentBean = JSONObject.parseObject(params, DepartmentBean.class);
        departmentService.addDepartment(departmentBean);
        return jsonObject;
    }

    /* *
     * @desc:修改科室
     * @author: dx
     * @date: 2019-09-09 10:02:33
     * @param params :
     * @return: com.alibaba.fastjson.JSONObject
     * @throws:
     * @version: 1.0
     **/
    @PostMapping("/updateDepartment")
    public JSONObject updateDepartment(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        DepartmentBean departmentBean = JSONObject.parseObject(params, DepartmentBean.class);
        departmentService.updateDepartment(departmentBean);
        return jsonObject;
    }

    @PostMapping("/updateDepartmentById")
    public JSONObject updateDepartmentById(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        List<DepartmentBean> departmentBeans = JSONObject.parseArray(params, DepartmentBean.class);
        departmentService.updateDepartmentById(departmentBeans);
        return jsonObject;
    }

    //查询部门的欠费情况
    @PostMapping("/queryArrearsByDept")
    public JSONObject queryArrearsByDept(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        DepartmentBean departmentBean = JSONObject.parseObject(params, DepartmentBean.class);
        jsonObject.put(Constant.Result.RETDATA, departmentService.queryArrearsByDept(departmentBean));
        return jsonObject;
    }
}
