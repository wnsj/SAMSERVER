package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ConfirmClosedDto {
    @ApiModelProperty(value = "患者住院号")
    private String hospNum;

    @ApiModelProperty(value = "出院时间")
    private String outHosp;
}
