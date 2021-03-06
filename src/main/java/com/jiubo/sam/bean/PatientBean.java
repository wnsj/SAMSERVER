package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 患者基础信息Bean
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PATIENT")
public class PatientBean implements Serializable {

    private static final long serialVersionUID = 1L;

    //患者id
    @TableId(value = "PATIENT_ID", type = IdType.AUTO)
    private String patientId;

    //住院号
    @ApiModelProperty(value = "住院号")
    private String hospNum;

    //姓名
    @ApiModelProperty(value = "姓名")
    private String name;

    //性别
    @ApiModelProperty(value = "性别")
    private String sex;

    //年龄
    @ApiModelProperty(value = "年龄")
    private String age;

    //住院日期
    @ApiModelProperty(value = "住院日期")
    private String hospTime;

    //是否在院【1:在院，0:出院】
    @ApiModelProperty(value = "是否在院【1:在院，0:出院】")
    private String inHosp;

    //出院时间
    @ApiModelProperty(value = "出院时间")
    private String outHosp;

    //科室id
    @ApiModelProperty(value = "科室id")
    private String deptId;

    @TableField(exist = false)
    private List<String> deptList;

    //科室名
    @ApiModelProperty(value = "科室名")
    @TableField(exist = false)
    private String deptName;

    //押金余额
    @ApiModelProperty(value = "预交金余额")
    @TableField(exist = false)
    private Double money;

    //每月应收
    @ApiModelProperty(value = "每月应收")
    private String receivable;

    //修改时间
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    //维护人
    @ApiModelProperty(value = "维护人")
    private String empId;

    //交费信息
    @ApiModelProperty(value = "交费信息")
    @TableField(exist = false)
    private List<PaymentBean> paymentList;

    //患者类型Id
    @ApiModelProperty(value = "患者类型Id")
    private String patitypeid;

    //患者类型名
    @ApiModelProperty(value = "患者类型名")
    @TableField(exist = false)
    private String patitypename;

    //医保类型Id
    @ApiModelProperty(value = "医保类型Id")
    private String mitypeid;

    //医保类型名
    @ApiModelProperty(value = "医保类型名")
    @TableField(exist = false)
    private String mitypename;

    //操作人
    @ApiModelProperty(value = "操作人")
    private String accountId;

    //操作账号
    @ApiModelProperty(value = "操作账号")
    @TableField(exist = false)
    private String accountName;

    //时差
    @ApiModelProperty(value = "时差")
    @TableField(exist = false)
    private String dateDiffrent;

    //最近缴费结束时间
    @ApiModelProperty(value = "最近缴费结束时间")
    @TableField(exist = false)
    private String endDate;

    @TableField(exist = false)
    private String begHospTime;

    @TableField(exist = false)
    private String endHospTime;

    @TableField(exist = false)
    private String payServiceArr[];

    //收费项目是否启动
    @ApiModelProperty(value = "收费项目是否启动")
    @TableField(exist = false)
    private Integer isStart;

    @TableField(exist = false)
    private String page;

    @TableField(exist = false)
    private String pageSize;

    @TableField(exist = false)
    private String empName;

    //是否合并数据【1：是】
    @ApiModelProperty(value = "是否合并数据【1：是】")
    @TableField(exist = false)
    private String isMerge;

    //非医疗费汇总
    @ApiModelProperty(value = "非医疗费汇总")
    @TableField(exist = false)
    private String medicalTatol;

    //医疗费汇总
    @ApiModelProperty(value = "医疗费汇总")
    @TableField(exist = false)
    private String paymentArrears;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "住院费余额",required = false,hidden = true)
    private BigDecimal hospBalance;

//    @ApiModelProperty(value = "预交金")
//    private BigDecimal deposit;
//
//    @ApiModelProperty(value = "预交金余额")
//    private BigDecimal depositBalance;

    @ApiModelProperty(value = "联系人")
    private String liaisonMan;

    @ApiModelProperty(value = "联系方式")
    private String liaisonManPhone;

    @ApiModelProperty(value = "患者联系方式")
    private String patientPhone;

    @ApiModelProperty(value = "单位名称")
    private String unitName;

    @ApiModelProperty(value = "单位地址")
    private String unitAddress;

    @ApiModelProperty(value = "来源(1:his；2:sam)",required = false,hidden = true)
    private Integer source;

    @ApiModelProperty(value = "创建时间",required = false,hidden = true)
    private Date createDate;

    @ApiModelProperty(value = "创建人",required = false,hidden = true)
    private Integer creator;

    @ApiModelProperty(value = "修改人",required = false,hidden = true)
    private Integer reviser;

    @ApiModelProperty(value = "是否删除（1：删除；2：未删除）",required = false,hidden = true)
    private Integer flag;

    @ApiModelProperty(value = "HIS流水")
    private String hisWaterNum;

    @ApiModelProperty(value = "是否特批【1:是;2:否】")
    private Integer isNoFunding;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatientBean that = (PatientBean) o;
        return Objects.equals(hospNum, that.hospNum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hospNum);
    }
}
