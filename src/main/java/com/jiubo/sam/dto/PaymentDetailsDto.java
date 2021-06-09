package com.jiubo.sam.dto;

import com.jiubo.sam.bean.PaymentDetailsBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PaymentDetailsDto extends PaymentDetailsBean {
    @ApiModelProperty(value = "预交金缴费合计")
    private Double marginUseTotal;

    @ApiModelProperty(value = "住院发生合计")
    private Double hospitalUseTotal;

    @ApiModelProperty(value = "门诊发生合计")
    private Double patientUseUseTotal;

    @ApiModelProperty(value = "非医疗费合计")
    private Double nonMedicalUseTotal;

    @ApiModelProperty(value = "预交金余额合计")
    private Double marginAmountUseTotal;


}
