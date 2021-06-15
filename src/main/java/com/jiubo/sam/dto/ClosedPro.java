package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ClosedPro {
    @ApiModelProperty(value = "项目名称")
    private String proName;

    @ApiModelProperty(value = "收费类型【0,默认计费 1，区间计费】")
    private Integer payType;

    @ApiModelProperty(value = "上一次关闭时间")
    private Date nextCloseDate;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;
}
