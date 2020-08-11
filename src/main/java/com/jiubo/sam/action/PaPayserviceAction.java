package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PaPayserviceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author mwl
 * @since 2020-08-10
 */
@Controller
@RequestMapping("/paPayserviceBean")
public class PaPayserviceAction {

    @Autowired
    PaPayserviceService paPayserviceService;

    //添加或修改缴费项目
    @PostMapping("/addUpdatePp")
    ///employeeAction/addUpdateEmp?[{empId:8,empName:'xiaoqiang8',sex:2,posId:2,isuse:1}]
    public JSONObject addAndUpdatePaPayService(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        List<PaPayserviceBean> paPayserviceBeans = JSONArray.parseArray(params, PaPayserviceBean.class);
        paPayserviceService.addAndUpdatePaPayService(paPayserviceBeans);
        return jsonObject;
    }

    //查询缴费项目
    @PostMapping("/queryPaPayService")
    public JSONObject queryPaPayService(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PaPayserviceBean paPayserviceBean = JSONObject.parseObject(params,PaPayserviceBean.class);
        jsonObject.put("retData",paPayserviceService.queryPaPayService(paPayserviceBean));
        return jsonObject;
    }
}
