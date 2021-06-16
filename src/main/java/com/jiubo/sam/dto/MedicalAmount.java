package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MedicalAmount {

    @ApiModelProperty(value = "医疗总发生")
    private BigDecimal medicalTotal;

    @ApiModelProperty(value = "住院总发生")
    private BigDecimal inHospitalTotal;

    @ApiModelProperty(value = "门诊总发生")
    private BigDecimal outpatientTotal;
}
