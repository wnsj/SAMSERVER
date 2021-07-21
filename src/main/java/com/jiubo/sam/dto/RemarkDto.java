package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RemarkDto {

    @ApiModelProperty(value = "主键id",required = true)
    private Integer id;

    @ApiModelProperty(value = "备注")
    private String remark;
}
