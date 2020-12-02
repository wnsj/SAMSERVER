package com.jiubo.sam.action;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.service.CommonService;
import com.jiubo.sam.util.TimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @desc:
 * @date: 2019-09-07 13:49
 * @author: dx
 * @version: 1.0
 */
@Slf4j
@Api(value = "测试类", tags = "456")
@RestController
@Scope("prototype")
@RequestMapping("/testAction")
public class TestAction {

    @Autowired
    private CommonService commonService;

    //单字段简单验证
    @GetMapping("/test")
    @ApiOperation(value = "测试方法", notes = "用于测试")
    public JSONObject testMethod(String str) {
        JSONObject jsonObject = new JSONObject();
        //jsonObject.put("time1", commonService.getDBTime());
        log.error("测试打版");
        jsonObject.put("time2", TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()));
        return jsonObject;
    }

    @GetMapping("test1")
    public Object test1(String name, Integer age, Integer idcard) {
        return Boolean.TRUE;
    }

    //
    @ApiOperation(value = "测试方法", notes = "用于测试")
    @GetMapping("/test2")
    public JSONObject test2(Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("time1", "测试方法 2...");
        return jsonObject;
    }

    @GetMapping("/test3")
    public String test3() {
        return "test 3";
    }
}
