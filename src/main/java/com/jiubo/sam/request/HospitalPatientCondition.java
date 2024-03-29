package com.jiubo.sam.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HospitalPatientCondition {

    @ApiModelProperty(value = "页码")
    private Integer pageNum;

    @ApiModelProperty(value = "条数")
    private Integer pageSize;

    @ApiModelProperty(value = "住院1门诊2")
    private Integer type;

    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "姓名")
    private String patientName;

    @ApiModelProperty(value = "部门ID")
    private Integer deptId;

    @ApiModelProperty(value = "部门ID集合")
    private List<Integer> deptList;

    @ApiModelProperty(value = "患者类型")
    private Integer patiTypeId;

    @ApiModelProperty(value = "医保类型")
    private Integer miTypeId;

    @ApiModelProperty(value = "维护医生")
    private Integer empId;

    @ApiModelProperty(value = "是否在院(1在院0不在")
    private Integer isInHospital;

    @ApiModelProperty(value = "是否欠费(1欠费2不欠费")
    private Integer isArrearage;

    @ApiModelProperty(value = "筛选缴费时间的开始时间")
    private Date begCreateDate;

    @ApiModelProperty(value = "筛选缴费时间的结束时间")
    private Date endCreateDate;

    @ApiModelProperty(value = "筛选结束时间的开始时间")
    private Date begDate;

    @ApiModelProperty(value = "筛选结束时间的结束时间")
    private Date endDate;

    @ApiModelProperty(value = "1查住院最新缴费")
    private Integer isNew;

    @ApiModelProperty(value = "筛选入院时间的开始时间")
    private Date hosBegDate;

    @ApiModelProperty(value = "筛选入院时间的结束时间")
    private Date hosEndDate;

    @ApiModelProperty(value = "筛选出院时间的开始时间")
    private Date outHosBegDate;

    @ApiModelProperty(value = "筛选出院时间的结束时间")
    private Date outHosEndDate;

    @ApiModelProperty(value = "身份证号")
    private String idCard;

    @ApiModelProperty(value = "交易流水号")
    private String serialNumber;

    @ApiModelProperty(value = "支付方式：1现金，2银行卡，3微信，4支付宝")
    private Integer payment;

    @ApiModelProperty(value = "1:缴费;2:退费")
    private Integer consumType;
}
