package com.jiubo.sam.dto;

import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.PaymentDetailsBean;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PdByPIdDto {
    @ApiModelProperty(value = "明细")
    private PageInfo<PaymentDetailsBean> pdByPId;

    @ApiModelProperty(value = "押金发生合计")
    private BigDecimal marginUseMax;

    @ApiModelProperty(value = "门诊发生合计")
    private BigDecimal patientUseMax;

    @ApiModelProperty(value = "住院发生合计")
    private BigDecimal hospitalUseMax;

    @ApiModelProperty(value = "非医疗发生合计")
    private BigDecimal noMeUseMax;

    @ApiModelProperty(value = "总数量")
    private Integer sumTotal;
}
