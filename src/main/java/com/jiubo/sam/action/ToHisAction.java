package com.jiubo.sam.action;


import cn.hutool.json.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.dto.CACondition;
import com.jiubo.sam.dto.CheckAccount;
import com.jiubo.sam.service.ToHisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @ApiOperation(value = "添加/退住院费/添加/退门诊费")
    @PostMapping("refundOrAddHP")
    public int refundOrAddHP(@RequestBody JSONObject jsonObject) throws Exception {
        return toHisService.refundOrAddHP(jsonObject);
    }

    @ApiOperation(value = "获取对账单")
    @PostMapping("getCATable")
    public PageInfo<CheckAccount> getCATable(@RequestBody CACondition condition) {
        return new PageInfo<CheckAccount>(toHisService.getCATable(condition));
    }
}
