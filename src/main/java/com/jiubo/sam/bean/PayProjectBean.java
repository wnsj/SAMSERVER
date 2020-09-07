package com.jiubo.sam.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PayProjectBean {

    private String proId;

    private String proName;

    private BigDecimal charge;
}
