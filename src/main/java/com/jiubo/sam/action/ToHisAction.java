package com.jiubo.sam.action;


import cn.hutool.json.JSONObject;
import com.jiubo.sam.service.ToHisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "与his对接接口")
@RestController
@RequestMapping("/toHisAction")
public class ToHisAction {

    @Autowired
    private ToHisService toHisService;

    @ApiOperation(value = "his添加患者")
    @PostMapping("addHisEmp")
    public int addHisEmp(@RequestBody JSONObject jsonObject){
        return toHisService.addHisEmp(jsonObject);
    }
}
