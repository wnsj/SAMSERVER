package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.HospitalPatientBean;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.request.HospitalPatientCondition;
import com.jiubo.sam.service.HospitalPatientService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 住院门诊 前端控制器
 * </p>
 *
 * @author dx
 * @since 2021-04-10
 */
@Api(tags = "住院门诊接口")
@RestController
@RequestMapping("/hospitalPatient")
public class HospitalPatientController {

    @Autowired
    private HospitalPatientService hospitalPatientService;

    @ApiOperation(value = "住院费或门诊费缴费")
    @PostMapping("addHospitalPatient")
    public JSONObject addHospitalPatient(@RequestBody HospitalPatientBean hospitalPatientBean) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        hospitalPatientService.addHospitalPatient(hospitalPatientBean);
        return jsonObject;
    }

    @ApiOperation(value = "住院费或门诊费缴费最新缴费查询")
    @PostMapping("findHospitalPatient")
    public JSONObject findHospitalPatient(@RequestBody HospitalPatientCondition hospitalPatientCondition) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA,hospitalPatientService.findHospitalPatient(hospitalPatientCondition));
        return jsonObject;
    }

    @ApiOperation(value = "修改住院费或门诊费缴费")
    @PostMapping("updateHospitalPatient")
    public JSONObject updateHospitalPatient(@RequestBody HospitalPatientBean hospitalPatientBean) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        hospitalPatientService.updateHospitalPatient(hospitalPatientBean);
        return jsonObject;
    }

}
