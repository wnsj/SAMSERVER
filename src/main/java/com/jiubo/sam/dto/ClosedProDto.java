package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ClosedProDto {

    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "关闭时间")
    private String endDate;
}
