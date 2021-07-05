package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class NoMeFeeDto {

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "关闭时间")
    private Date endDate;

    @ApiModelProperty(value = "原价")
    private BigDecimal originalPrice;

    @ApiModelProperty(value = "现价")
    private BigDecimal presentPrice;

    @ApiModelProperty(value = "应收")
    private BigDecimal receivable;

    @ApiModelProperty(value = "实收")
    private BigDecimal netReceipts;
}
