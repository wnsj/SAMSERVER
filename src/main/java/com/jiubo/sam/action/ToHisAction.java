package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.dto.CACondition;
import com.jiubo.sam.dto.CheckAccount;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.ToHisService;
import com.jiubo.sam.util.WebApiUtil;
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
    public int addHisEmp(@RequestBody String param) throws MessageException {
        WebApiUtil.WriteStringToFile(param, "addHisEmp");
        return toHisService.addHisEmp(param);
    }

    @ApiOperation(value = "添加/退住院费/添加/退门诊费")
    @PostMapping("refundOrAddHP")
    public JSONObject refundOrAddHP(@RequestBody String param) throws Exception {
        WebApiUtil.WriteStringToFile(param, "refundOrAddHP");
        JSONObject object = toHisService.refundOrAddHP(param);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA,object.toJSONString());
        return jsonObject;
    }

    @ApiOperation(value = "获取对账单")
    @PostMapping("getCATable")
    public PageInfo<CheckAccount> getCATable(@RequestBody CACondition condition) {
        return new PageInfo<CheckAccount>(toHisService.getCATable(condition));
    }
}
