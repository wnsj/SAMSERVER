package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

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

    @TableField(exist = false)
    private String deptId;

    private String accountId;
}
