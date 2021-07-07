package com.jiubo.sam.action;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.dto.FromHisPatient;
import com.jiubo.sam.dto.NoMeInitDto;
import com.jiubo.sam.schedule.ToHisTask;
import com.jiubo.sam.service.CommonService;
import com.jiubo.sam.util.TimeUtil;
import com.jiubo.sam.util.WebApiUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * @desc:
 * @date: 2019-09-07 13:49
 * @author: dx
 * @version: 1.0
 */
@Slf4j
@Api(value = "测试类", tags = "测试类")
@RestController
@Scope("prototype")
@RequestMapping("/testAction")
public class TestAction {

    @Autowired
    private CommonService commonService;

    @Autowired
    private ToHisTask toHisTask;

    @Autowired
    private PatientDao patientDao;

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
    public NoMeInitDto test3() {
        return new NoMeInitDto();
    }

    @GetMapping("/testWebService")
    public void testWebService() {
        String method = "Z000";
        String sendJson = "{\"BalanceMoney\": 500}";
        Object[] objs = new Object[2];
        objs[0] = method;
        objs[1] = sendJson;
        Object[] methods = WebApiUtil.execWebService("http://yfzx.bsesoft.com:8002/sjservice.asmx?wsdl", "CallWebMethod", objs);
        log.error("数据" + Arrays.toString(methods));
    }

    @ApiOperation(value = "同步患者信息")
    @GetMapping("/syncPatientAndAddHP")
    public void syncPatientAndAddHP() throws Exception {
        toHisTask.syncPatientAndAddHP();
    }

    @ApiOperation(value = "同步科室信息")
    @GetMapping("/syncDept")
    public void syncDept() {
        toHisTask.syncDept();
    }

    @ApiOperation(value = "同步员工信息")
    @GetMapping("/syncEmployee")
    public void syncEmployee() throws IOException {
        toHisTask.syncEmployee();
    }

//    @ApiOperation(value = "读取文件数据")
//    @GetMapping("/readFile")
//    public String readFile(@RequestParam("path") String path, @RequestParam("dataKey") String dataKey) {
//        String fileToString = WebApiUtil.ReaderFileToString(path);
//        JSONObject jsonObject = JSONObject.parseObject(fileToString);
//        JSONArray jsonArray = jsonObject.getJSONArray(dataKey);
//        return fileToString;
//    }

    @PostMapping("/patchPa")
    public void patchPa(@RequestBody FromHisPatient fromHisPatient) {
        patientDao.patchPa(fromHisPatient);
    }
}
