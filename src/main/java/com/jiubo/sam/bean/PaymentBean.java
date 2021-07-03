package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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
    @ApiModelProperty(value = "患者Id")
    private String patientId;

    //收费项目Id
    @ApiModelProperty(value = "收费项目Id")
    private String payserviceId;

    //费用类型（缴费：0或退费：1）
    @ApiModelProperty(value = "费用类型（缴费：0或退费：1）")
    private String paymentStatus;

    //收费项目Id
    @ApiModelProperty(value = "收费项目Id")
    private String deptId;

    //收费项目名
    @ApiModelProperty(value = "收费项目名")
    private String payserviceName;

    //每月应收
    @ApiModelProperty(value = "每月应收")
    private double receivable;

    //实际交费
    @ApiModelProperty(value = "实际交费")
    private double actualpayment;

    //开始时间
    @ApiModelProperty(value = "开始时间")
    private String begtime;

    //结束时间
    @ApiModelProperty(value = "结束时间")
    private String endtime;

    //交费日期
    @ApiModelProperty(value = "交费日期")
    private String paymentTime;

    //修改时间
    @ApiModelProperty(value = "修改时间")
    private String updatetime;


    //是否启用【1:启用， 0:不启用】
    @ApiModelProperty(value = "是否启用【1:启用， 0:不启用】")
    private Boolean isuse;

    //备注
    @ApiModelProperty(value = "备注")
    private String paymentRemark;

    //账户Id
    @ApiModelProperty(value = "账户Id")
    private String accountId;

    //单价
    @ApiModelProperty(value = "单价")
    private double price;

    //天数
    @ApiModelProperty(value = "天数")
    private int days;

    //时差
    @ApiModelProperty(value = "时差")
    private String dateDiffrent;

    //单项的总交费用
    @ApiModelProperty(value = "单项的总交费用")
    private String apTotle;

    private String empId;

    //缴费到期开始时间
    @ApiModelProperty(value = "缴费到期开始时间")
    @TableField(exist = false)
    private String endBegDate;

    //缴费到期结束时间
    @ApiModelProperty(value = "缴费到期结束时间")
    @TableField(exist = false)
    private String endEndDate;

    @TableField(exist = false)
    private double total;
    // 住院号
    @ApiModelProperty(value = "住院号")
    @TableField(exist = false)
    private String hospNum;

    //是否合并数据【1：是】
    @ApiModelProperty(value = "是否合并数据【1：是】")
    @TableField(exist = false)
    private String isMerge;

    //数据数量
    @ApiModelProperty(value = "数据数量")
    @TableField(exist = false)
    private String totalAmount;

    @ApiModelProperty(value = "时间")
    @TableField(exist = false)
    private String dateTime;
}
