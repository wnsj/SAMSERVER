package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.RoleMenuRefBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.service.RoleMenuRefService;
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
@RequestMapping("/roleMenuRefBean")
public class RoleMenuRefController {

    @Autowired
    private RoleMenuRefService roleMenuRefService;

    @PostMapping("/addRMRef")
    public JSONObject addRMRef(@RequestBody RoleMenuRefBean roleMenuRefBean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        roleMenuRefService.addRMRef(roleMenuRefBean);
        return jsonObject;
    }

    @PostMapping("/patchRMRef")
    public JSONObject patchRMRef(@RequestBody RoleMenuRefBean roleMenuRefBean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        roleMenuRefService.patchRMRef(roleMenuRefBean);
        return jsonObject;
    }
}

