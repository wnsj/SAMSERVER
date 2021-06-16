package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OpenServiceReceive {

    @ApiModelProperty(value = "项目id",required = true)
    private Integer proId;

    @ApiModelProperty(value = "开始日期",required = true)
    private Date startDate;

    @ApiModelProperty(value = "结束日期【若为区间计费必填】",required = false)
    private Date endDate;

    @ApiModelProperty(value = "单价",required = true)
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "计费类型",required = true)
    private Integer payType;

    @ApiModelProperty(value = "患者id",required = true)
    private Integer patientId;

    @ApiModelProperty(value = "住院号",required = true)
    private String hospNum;

    @ApiModelProperty(value = "身份证号",required = true)
    private String idCard;

    @ApiModelProperty(value = "操作人",required = true)
    private Integer creator;
}
