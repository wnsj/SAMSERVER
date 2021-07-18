package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class NoMeTotal {

    @ApiModelProperty(value = "患者id")
    private Integer patientId;

    @ApiModelProperty(value = "欠费天数【若为负数代表多交钱的天数】")
    private Integer days;

    @ApiModelProperty(value = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "总欠费")
    private BigDecimal total;

    @ApiModelProperty(value = "到期时间")
    private Date endTime;
}
