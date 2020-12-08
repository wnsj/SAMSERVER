package com.jiubo.sam.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PayTotalDto {
    private String hospNum;
    private BigDecimal total;
}
