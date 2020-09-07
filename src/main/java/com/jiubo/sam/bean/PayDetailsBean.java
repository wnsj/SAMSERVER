package com.jiubo.sam.bean;

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
    private String payTime;

    /**
     * 总的缴费
     */
    private BigDecimal payTotal;


    /**
     * 项目收费
     */
    private List<PayProjectBean> projectBeanList;
}
