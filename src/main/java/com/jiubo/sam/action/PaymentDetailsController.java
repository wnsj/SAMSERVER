package com.jiubo.sam.action;


import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.common.Constant;
import com.jiubo.sam.request.HospitalPatientCondition;
import com.jiubo.sam.service.PaymentDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 缴费明细 前端控制器
 * </p>
 *
 * @author dx
 * @since 2021-04-10
 */
@Api(tags = "缴费详情接口")
@RestController
@RequestMapping("/paymentDetailsBean")
public class PaymentDetailsController {

    @Autowired
    private PaymentDetailsService paymentDetailsService;


    @ApiOperation(value = "押金记录查询")
    @PostMapping("findPaymentDetail")
    public JSONObject findHospitalPatient(@RequestBody HospitalPatientCondition hospitalPatientCondition) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA,paymentDetailsService.findPaymentDetail(hospitalPatientCondition));
        return jsonObject;
    }


    @ApiOperation(value = "缴费明细查询")
    @PostMapping("findPaymentDetailByHos")
    public JSONObject findPaymentDetailByHos(@RequestBody HospitalPatientCondition hospitalPatientCondition) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA,paymentDetailsService.findPaymentDetailByHos(hospitalPatientCondition));
        return jsonObject;
    }


}
