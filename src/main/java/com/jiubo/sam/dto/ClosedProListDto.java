package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ClosedProListDto {
    @ApiModelProperty(value = "主键id")
    private List<ClosedProDto> closedProDtoList;

    @ApiModelProperty(value = "住院号")
    private String hospNum;


    @ApiModelProperty(value = "操作人")
    private String creator;
}
