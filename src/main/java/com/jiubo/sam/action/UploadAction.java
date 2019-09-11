package com.jiubo.sam.action;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.service.PatientService;
import com.jiubo.sam.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @desc:execl上传解析
 * @date: 2019-09-11 10:23
 * @author: dx
 * @version: 1.0
 */
@Controller
@RequestMapping("/uploadAction")
public class UploadAction {

    @Autowired
    private PatientService patientService;

    //上传患者基本信息
    @ResponseBody
    @RequestMapping("/uploadPatient")
    public JSONObject uploadPatient(String fileName, MultipartFile file){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        Map<Object, Object> objectObjectMap = ExcelUtil.updateExcel(fileName, file, true);
        patientService.addPatientList(objectObjectMap);
        return jsonObject;
    }
}
