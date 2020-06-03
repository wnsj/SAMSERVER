package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

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

    //患者Id
    private String patientId;

    //收费项目Id
    private String payserviceId;

    //收费项目Id
    private String deptId;

    //收费项目名
    private String payserviceName;

    //每月应收
    private double receivable;

    //实际交费
    private double actualpayment;

    //开始时间
    private String begtime;

    //结束时间
    private String endtime;

    //交费日期
    private String paymenttime;

    //修改时间
    private String updatetime;


    //是否启用【1:启用， 0:不启用】
    private Boolean isuse;

    //备注
    private String paymentRemark;

    //账户Id
    private String accountId;

    //单价
    private double price;

    //天数
    private int days;

    //时差
    private String dateDiffrent;

    //单项的总交费用
    private String apTotle;

    //缴费到期开始时间
    @TableField(exist = false)
    private String endBegDate;

    //缴费到期结束时间
    @TableField(exist = false)
    private String endEndDate;
}
