package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 交费 Bean
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PAYMENT")
public class PaymentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "PAYMENT_ID", type = IdType.AUTO)
    private String paymentId;

    private String patientId;

    private String payserviceId;

    private double receivable;

    private double actualpayment;

    private String begtime;

    private String endtime;

    private String paymenttime;

    private String updatetime;


}
