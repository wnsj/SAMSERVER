package com.jiubo.sam.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PayDetailsBean {

    /**
     * 缴费时间
     */
    @ApiModelProperty(value = "缴费时间")
    private String payTime;

    /**
     * 总的缴费
     */
    @ApiModelProperty(value = "总的缴费")
    private BigDecimal payTotal;


    /**
     * 项目收费
     */
    @ApiModelProperty(value = "项目收费")
    private List<PayProjectBean> projectBeanList;

    /*缴费，退费*/
    @ApiModelProperty(value = "缴费，退费")
    private String paymentStatus;

}
