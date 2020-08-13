package com.jiubo.sam.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class PayCount {
    private BigDecimal totalCount;
    private List<PayDetailsBean> payDetailsBeanList;
}
