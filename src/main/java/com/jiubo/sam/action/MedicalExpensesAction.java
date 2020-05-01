package com.jiubo.sam.action;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.MedicalExpensesBean;
import com.jiubo.sam.common.Constant;
import org.springframework.context.annotation.Scope;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.MedicalExpensesService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 医疗费
 * </p>
 *
 * @author mwl
 * @since 2020-04-21
 */
@RestController
@Scope("prototype")
@RequestMapping("/MedicalExpenses")
public class MedicalExpensesAction {
    @Autowired
    private MedicalExpensesService medicalExpensesService;

    //查询医保类型
    @PostMapping("/queryMedicinsurtype")
    public JSONObject queryMedicinsurtype(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        MedicalExpensesBean medicalExpensesBean = JSONObject.parseObject(params, MedicalExpensesBean.class);
        jsonObject.put(Constant.Result.RETDATA, medicalExpensesService.queryMedicalExpenses(medicalExpensesBean));
        return jsonObject;
    }

    //添加医保类型
    @PostMapping("/addMedicinsurtype")
    public JSONObject addMedicinsurtype(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        MedicalExpensesBean medicalExpensesBean = JSONObject.parseObject(params, MedicalExpensesBean.class);
        medicalExpensesService.addMedicalExpenses(medicalExpensesBean);
        return jsonObject;
    }

    //修改医保类型
    @PostMapping("/updateMedicinsurtype")
    public JSONObject updateMedicinsurtype(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        MedicalExpensesBean medicalExpensesBean = JSONObject.parseObject(params, MedicalExpensesBean.class);
        medicalExpensesService.updateMedicalExpenses(medicalExpensesBean);
        return jsonObject;
    }
}
