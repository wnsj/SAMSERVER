package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class OpenPro extends ClosedPro{

    @ApiModelProperty(value = "开始时间")
    private Date startDate;
}
