package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.AdmissionRecordsBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.AdmissionRecordsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 入院记录表 前端控制器
 * </p>
 *
 * @author dx
 * @since 2020-10-17
 */
@RestController
@RequestMapping("/admissionRecordsBean")
public class AdmissionRecordsController {

    @Autowired
    private AdmissionRecordsService admissionRecordsService;

    //查询入院记录
    @PostMapping("/queryAdmissionRecord")
    public JSONObject queryAdmissionRecord(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        AdmissionRecordsBean admissionRecordsBean = JSONObject.parseObject(params, AdmissionRecordsBean.class);
        jsonObject.put(Constant.Result.RETDATA, admissionRecordsService.queryAdmissionRecord(admissionRecordsBean));
        return jsonObject;
    }
}
