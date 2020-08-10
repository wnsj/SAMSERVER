package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.AccountBean;
import com.jiubo.sam.bean.PositionBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PositionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Action;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author syl
 * @since 2020-08-07
 */
@RestController
@RequestMapping("/positionBean")
public class PositionController {

    @Autowired
    private PositionService positionService;

    @PostMapping("/getPosByCondition")
    public JSONObject getPosByCondition(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PositionBean positionBean = JSONObject.parseObject(params, PositionBean.class);
        jsonObject.put(Constant.Result.RETDATA, positionService.getPosByCondition(positionBean));
        return jsonObject;
    }

    @PostMapping("/addPos")
    public JSONObject addPos(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PositionBean positionBean = JSONObject.parseObject(params, PositionBean.class);
        positionService.addPos(positionBean);
        return jsonObject;
    }

    @PostMapping("/patchPos")
    public JSONObject patchPos(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PositionBean positionBean = JSONObject.parseObject(params, PositionBean.class);
        positionService.patchPos(positionBean);
        return jsonObject;
    }

    @PostMapping("/getAllPos")
    public JSONObject getAllPos(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PositionBean positionBean = JSONObject.parseObject(params, PositionBean.class);
        jsonObject.put(Constant.Result.RETDATA, positionService.getAllPos(positionBean));
        return jsonObject;
    }
}
