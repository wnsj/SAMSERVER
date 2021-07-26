package com.jiubo.sam.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CheckAccount {

    @ApiModelProperty(value = "圣安身份证号")
    private String samIdCard;

    @ApiModelProperty(value = "圣安缴费金额")
    private BigDecimal samCharge;

    @ApiModelProperty(value = "圣安退费金额")
    private BigDecimal samRefund;

    @ApiModelProperty(value = "圣安SAM交易流水号")
    private String samSerialNumberSam;

    @ApiModelProperty(value = "圣安HIS交易流水号")
    private String samSerialNumberHis;

    @ApiModelProperty(value = "圣安交易时间")
    private Date samTradeDate;

    @ApiModelProperty(value = "圣安交易时间")
    private String samTradeDateFormat;

    @ApiModelProperty(value = "圣安支付方式")
    private String samPayMethod;

    @ApiModelProperty(value = "圣安操作人")
    private String samOperator;

    @ApiModelProperty(value = "圣安支付类型")
    private Integer samPayType;

    @ApiModelProperty(value = "圣安开发商")
    private String samDeveloper;

    @ApiModelProperty(value = "圣安交易类型")
    private String samChargeType;

    @ApiModelProperty(value = "HIS身份证号")
    private String hisIdCard;

    @ApiModelProperty(value = "HIS缴费金额")
    private BigDecimal hisCharge;

    @ApiModelProperty(value = "HIS退费金额")
    private BigDecimal hisRefund;

    @ApiModelProperty(value = "HIS->SAM交易流水号")
    private String hisSerialNumberSam;

    @ApiModelProperty(value = "HIS->HIS交易流水号")
    private String hisSerialNumberHis;

    @ApiModelProperty(value = "HIS交易时间")
    private String hisTradeDate;

    @ApiModelProperty(value = "HIS支付方式")
    private String hisPayMethod;

    @ApiModelProperty(value = "HIS操作人")
    private String hisOperator;

    @ApiModelProperty(value = "HIS支付类型")
    private Integer hisPayType;

    @ApiModelProperty(value = "HIS开发商")
    private String hisDeveloper;
}
