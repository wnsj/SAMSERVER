package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClosedPro {

    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "项目名称")
    private String proName;

    @ApiModelProperty(value = "收费类型【0,默认计费 1，区间计费】")
    private Integer payType;

    @ApiModelProperty(value = "上一次关闭时间")
    private String nextCloseDate;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;
}
