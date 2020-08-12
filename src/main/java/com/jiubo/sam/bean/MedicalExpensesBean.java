package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *  医保类型Bean
 * </p>
 *
 * @author mwl
 * @since 2020-04-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MEDICAL_EXPENSES")
public class MedicalExpensesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    //类型Id
    @TableId(value = "ME_ID", type = IdType.AUTO)
    private String meId;

    private String hospNum;

    private String depositFee;

    private String realFee;

    private String arrearsFee;

    private String createDate;

    private String begDate;

    private String endDate;

    private String days;

    @TableField(exist = false)
    private String patientName;

    @TableField(exist = false)
    private String deptName;

    private String deptId;

    private String accountId;

    // 孙云龙添加
    /**
     * 1:在院;2：出院
     */
    private Integer isInHospital;

    /**
     * 备注
     */
    private String remarks;

    @TableField(exist = false)
    private String isInHospitalLabel;

    @TableField(exist = false)
    private String patiTypeId;

    @TableField(exist = false)
    private String patiTypeName;

    @TableField(exist = false)
    private String miTypeId;

    @TableField(exist = false)
    private String miTypeName;

    @TableField(exist = false)
    private String begCreateDate;

    @TableField(exist = false)
    private String endCreateDate;

    // end
    //补缴总额
    @TableField(exist = false)
    private BigDecimal spMoney;

    //补缴查询开始时间
    @TableField(exist = false)
    private String spBegDate;

    //补缴查询结束时间
    @TableField(exist = false)
    private String spEndDate;

    @TableField(exist = false)
    private String inHosp;
}
