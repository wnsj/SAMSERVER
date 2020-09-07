package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PatientService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * 患者基础信息 前端控制器
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@RestController
@Scope("prototype")
@RequestMapping("/patientAction")
public class PatientAction {

    @Autowired
    private PatientService patientService;

    //根据住院号查询患者信息
    @PostMapping("/queryPatientByHospNum")
    public JSONObject queryPatientByHospNum(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatientBean patientBean = JSONObject.parseObject(params, PatientBean.class);
        jsonObject.put(Constant.Result.RETDATA, patientService.queryPatientByHospNum(patientBean));
        return jsonObject;
    }

    /**
     * 新增，查询患者汇总信息
     * @param params
     * @return
     * @throws Exception
     * author mwl
     * 2019-04-15
     */
    @PostMapping("/queryGatherNewPayment")
    public JSONObject queryGatherNewPayment(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatientBean patientBean = JSONObject.parseObject(params, PatientBean.class);
        jsonObject.put(Constant.Result.RETDATA, patientService.queryGatherNewPayment(patientBean));
        return jsonObject;
    }

    //根据住院号精确查询患者信息
    @PostMapping("/accurateQueryByHospNum")
    public JSONObject accurateQuery(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatientBean patientBean = JSONObject.parseObject(params, PatientBean.class);
        jsonObject.put(Constant.Result.RETDATA, patientService.accurateQuery(patientBean));
        return jsonObject;
    }

    //根据住院号精确查询患者信息
    @PostMapping("/queryPatient")
    public JSONObject queryPatient(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatientBean patientBean = JSONObject.parseObject(params, PatientBean.class);
        jsonObject.put(Constant.Result.RETDATA, patientService.queryPatient(patientBean.getPage(),patientBean.getPageSize(),patientBean));
        return jsonObject;
    }

    //根据住院号精确查询患者信息
    @PostMapping("/fuzzyQueryByHospNum")
    public JSONObject fuzzyQuery(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatientBean patientBean = JSONObject.parseObject(params, PatientBean.class);
        jsonObject.put(Constant.Result.RETDATA, patientService.fuzzyQuery(patientBean));
        return jsonObject;
    }

    //根据患者Id及收费时间查询患者信息及缴费信息
    @PostMapping("/queryPatientPaymentByIdTime")
    public JSONObject queryPatientPaymentByIdTime(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        Map<String, Object> map = JSONObject.parseObject(params, Map.class);
        jsonObject.put(Constant.Result.RETDATA, patientService.queryPatientPaymentByIdTime(map));
        return jsonObject;
    }

    //添加患者及收费项目
    @PostMapping("/addPatient")
    //{hospNum:'000002',name:'张三',sex:'1',age:18,inHosp:0,deptId:6,receivable:3500,paymentList:[{payserviceId:2,receivable:3500,begtime:'2019-10-01',endtime:'2019-10-31'}]}
    public JSONObject addPatient(@RequestBody String params) throws MessageException {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatientBean patientBean = JSONObject.parseObject(params, PatientBean.class);
        jsonObject.put(Constant.Result.RETDATA, patientService.addPatient(patientBean));
        return jsonObject;
    }

    //批量添加启动项
    @PostMapping("/startAndStopPayService")
    public JSONObject startUpPayService(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatientBean patientBean = JSONObject.parseObject(params, PatientBean.class);
        patientService.startUpPayService(patientBean);
        return jsonObject;
    }

    //查询患者欠费
    @PostMapping("/patientArrearse")
    public JSONObject patientArrearse(@RequestBody String params) throws Exception {
        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        PatientBean patientBean = JSONObject.parseObject(params, PatientBean.class);
        jsonObject.put(Constant.Result.RETDATA, patientService.patientArrears(patientBean));
        return jsonObject;
    }

    //查询患者信息
//    @PostMapping("/queryPatient")
//    public JSONObject queryPatient(@RequestBody String params) throws MessageException {
//        if (StringUtils.isBlank(params)) throw new MessageException("参数接收失败!");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
//        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
//        PatientBean patientBean = JSONObject.parseObject(params, PatientBean.class);
//        patientService.addPatient(patientBean);
//        return jsonObject;
//    }
}
