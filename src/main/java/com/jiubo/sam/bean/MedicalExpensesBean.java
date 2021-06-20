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
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 医保类型Bean
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

    //押金
    @ApiModelProperty(value = "押金")
    private String depositFee;

    //欠费金额（负值）
    @ApiModelProperty(value = "欠费金额（负值）")
    private String realFee;

    @TableField(exist = false)
    private String arrears;

    //预估欠费
    @ApiModelProperty(value = "预估欠费")
    private BigDecimal estimatedArrears;

    //补缴总额（正值）
    @ApiModelProperty(value = "补缴总额（正值）")
    private String arrearsFee;

    private String createDate;

    private String begDate;

    private String endDate;

    private String days;

    @TableField(exist = false)
    private int day;

    @TableField(exist = false)
    private String patientName;

    @TableField(exist = false)
    private String deptName;

    private String deptId;

    //部门LIST_ID
    @ApiModelProperty(value = "部门LIST_ID")
    @TableField(exist = false)
    private List<String> deptList;


    private String accountId;

    // 孙云龙添加
    /**
     * 1:在院;2：出院
     */
    @ApiModelProperty(value = "1:在院;2：出院")
    private Integer isInHospital;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remarks;

    private String empId;

    @TableField(exist = false)
    private String empName;

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
    //待补缴总额
    @ApiModelProperty(value = "待补缴总额")
    @TableField(exist = false)
    private BigDecimal spMoney;

    //补缴查询开始时间
    @ApiModelProperty(value = "补缴查询开始时间")
    @TableField(exist = false)
    private String spBegDate;

    //补缴查询结束时间
    @ApiModelProperty(value = "补缴查询结束时间")
    @TableField(exist = false)
    private String spEndDate;

    @TableField(exist = false)
    private String inHosp;

    //是否欠费
    @ApiModelProperty(value = "是否欠费")
    @TableField(exist = false)
    private String isArrears;

    @TableField(exist = false)
    private String hasShow;

    //查询到的数据行数
    @ApiModelProperty(value = "查询到的数据行数")
    @TableField(exist = false)
    private Integer totalAmount;

    @TableField(exist = false)
    private Integer pageNum;

    @TableField(exist = false)
    private Integer pageSize;

    //是否只看补缴
    @ApiModelProperty(value = "是否只看补缴")
    @TableField(exist = false)
    private Boolean isChecked;

    /**
     * 每期欠费累计
     */
    @ApiModelProperty(value = "每期欠费累计")
    @TableField(exist = false)
    private String realFeeTotle;

    /**
     * 补缴总额
     */
    @ApiModelProperty(value = "补缴总额")
    @TableField(exist = false)
    private String arrearsFeeTotle;

    /**
     * 总押金
     */
    @ApiModelProperty(value = "总押金")
    @TableField(exist = false)
    private String depositFeeTotle;

    /**
     * 实际总欠费
     */
    @ApiModelProperty(value = "实际总欠费")
    @TableField(exist = false)
    private String spFeeTotal;

    /**
     * 账户余额
     */
    @ApiModelProperty(value = "账户余额")
    @TableField(exist = false)
    private String balance;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "年龄")
    @TableField(exist = false)
    private Integer age;

    /**
     * 性别
     */
    @ApiModelProperty(value = "性别")
    @TableField(exist = false)
    private Integer sex;
}
