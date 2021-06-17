package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProDto {
    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "开始时间")
    private String begDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;

    @ApiModelProperty(value = "单价")
    private String unitPrice;
}
