package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PatientMoneyCount {

    @ApiModelProperty(value = "预交金余额")
    private BigDecimal depositBalance;

    @ApiModelProperty(value = "医疗总发生")
    private BigDecimal medical;

    @ApiModelProperty(value = "非医疗总发生")
    private BigDecimal nonMedical;
}
