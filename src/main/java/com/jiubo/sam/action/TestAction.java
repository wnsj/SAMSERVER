package com.jiubo.sam.action;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.service.CommonService;
import com.jiubo.sam.util.TimeUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @desc:
 * @date: 2019-09-07 13:49
 * @author: dx
 * @version: 1.0
 */

@Api(value = "测试类", tags = "456")
@RestController
@Scope("prototype")
@RequestMapping("/testAction")
public class TestAction {

    @Autowired
    private CommonService commonService;

    @ApiOperation(value = "测试方法", notes = "用于测试")
    @GetMapping("/test")
    public JSONObject testMethod(@ApiParam(name = "传入对象", value = "{str:123}") @RequestBody String str) {
        JSONObject jsonObject = new JSONObject();
        //jsonObject.put("time1", commonService.getDBTime());
        jsonObject.put("time2", TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(TimeUtil.getDBTime()));
        return jsonObject;
    }
}
