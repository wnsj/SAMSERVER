package com.jiubo.sam.dto;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CaTableDto {

    private PageInfo<CheckAccount> list;

    @ApiModelProperty(value = "圣安缴费金额总额")
    private BigDecimal samChargeMax;

    @ApiModelProperty(value = "圣安退费金额总额")
    private BigDecimal samRefundMax;

    @ApiModelProperty(value = "HIS缴费金额总额")
    private BigDecimal hisChargeMax;

    @ApiModelProperty(value = "HIS退费金额总额")
    private BigDecimal hisRefundMax;
}
