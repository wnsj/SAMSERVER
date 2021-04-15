package com.jiubo.sam.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PrintRequest {

    @ApiModelProperty(value = "类型")
    private Integer type;

    @ApiModelProperty(value = "数据主键")
    private Integer detailsId;
}
