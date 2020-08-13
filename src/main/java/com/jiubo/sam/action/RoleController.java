package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;

import com.jiubo.sam.bean.RoleBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author dx
 * @since 2020-05-20
 */
@RestController
@RequestMapping("/roleBean")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/getAllRole")
    public JSONObject getAllRole() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA,roleService.getAllRole());
        return jsonObject;
    }

    @PostMapping("/getRoleByCondition")
    public JSONObject getRoleByCondition(@RequestBody RoleBean roleBean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA,roleService.getRoleByCondition(roleBean));
        return jsonObject;
    }


    @PostMapping("/addRole")
    public JSONObject addRole(@RequestBody RoleBean roleBean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        roleService.addRole(roleBean);
        return jsonObject;
    }

    @PostMapping("/patchRoleById")
    public JSONObject patchRoleById(@RequestBody RoleBean roleBean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        roleService.patchRoleById(roleBean);
        return jsonObject;
    }
}
