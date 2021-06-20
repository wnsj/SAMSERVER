package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PdCondition {

    @ApiModelProperty(value = "患者身份证号", required = true)
    private String idCard;

    @ApiModelProperty(value = "科室")
    private Integer deptId;

    @ApiModelProperty(value = "开始时间")
    private Date startDate;

    @ApiModelProperty(value = "结束时间")
    private Date endDate;

    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "每页条数")
    private Integer pageSize;
}
