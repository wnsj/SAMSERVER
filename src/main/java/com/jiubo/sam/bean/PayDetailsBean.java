package com.jiubo.sam.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

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
     * 治疗费
     */
    private BigDecimal payTreatment;

    /**
     * 伙食费
     */
    private BigDecimal payBoard;

    /**
     * 采暖费
     */
    private BigDecimal payHeating;

    /**
     * 卫生费
     */
    private BigDecimal payHygiene;

    /**
     * 病号服
     */
    private BigDecimal payPatientSuit;

    /**
     * 监护费
     */
    private BigDecimal payGuardianship;

    /**
     *单间费
     */
    private BigDecimal paySingleRoom;

    /**
     * 医护费
     */
    private BigDecimal payMedical;


    /**
     * 医疗费
     */
    private BigDecimal payMedicalFee;

    /**
     * 其他
     */
    private BigDecimal other;
}
