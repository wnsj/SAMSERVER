package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;

import com.jiubo.sam.bean.MenuBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.service.MenuService;
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
 * @author syl
 * @since 2020-05-21
 */
@RestController
@RequestMapping("/menuBean")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping("/getAllMenu")
    public JSONObject getAllMenu(@RequestBody MenuBean menuBean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA,menuService.getAllMenu(menuBean));
        return jsonObject;
    }

    @PostMapping("/addMenu")
    public JSONObject addMenu(@RequestBody MenuBean menuBean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        menuService.addMenu(menuBean);
        return jsonObject;
    }

    @PostMapping("/patchMenuById")
    public JSONObject patchMenuById(@RequestBody MenuBean menuBean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        menuService.patchMenuById(menuBean);
        return jsonObject;
    }

    @PostMapping("/getMenu")
    public JSONObject getMenu() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA,menuService.getMenu());
        return jsonObject;
    }

    @PostMapping("/getMenuByPage")
    public JSONObject getMenuByPage(@RequestBody MenuBean menuBean) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA,menuService.getMenuByPage(menuBean));
        return jsonObject;
    }
}
