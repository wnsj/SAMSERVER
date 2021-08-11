package com.jiubo.sam.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentOne {
    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "入院时间")
    private String hospDate;

    @ApiModelProperty(value = "出院时间")
    private String outHospDate;

    @ApiModelProperty(value = "在院状态【1：在院；0：出院】")
    private Integer inHosp;

    @ApiModelProperty(value = "患者名字")
    private String name;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "性别【1：男；2：女】")
    private Integer sex;

    @ApiModelProperty(value = "缴退类型【0：缴费；1：退费】")
    private String paymentStatus;

    @ApiModelProperty(value = "科室名字")
    private String deptName;

    @ApiModelProperty(value = "维护人")
    private String empName;

    @ApiModelProperty(value = "患者类型")
    private String patientType;

    @ApiModelProperty(value = "医保类型")
    private String medicSurType;

    @ApiModelProperty(value = "缴费时间")
    private String payDate;

//    @ApiModelProperty(value = "间隔天数")
//    private Integer days;
//
    @ApiModelProperty(value = "实收")
    private BigDecimal realCross;

    @ApiModelProperty(value = "操作人")
    private String operation;

    @ApiModelProperty(value = "单价")
    private BigDecimal unitPrice;

    @ApiModelProperty(value = "应收")
    private BigDecimal receivable;

    @ApiModelProperty(value = "开始时间")
    private String startDate;

    @ApiModelProperty(value = "结束时间")
    private String endDate;
}
