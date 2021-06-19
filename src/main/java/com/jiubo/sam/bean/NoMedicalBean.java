package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author dx
 * @since 2021-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("no_medical")
@ApiModel(value="NoMedicalBean对象", description="")
public class NoMedicalBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "患者身份证号")
    private String idCard;

    @ApiModelProperty(value = "缴费日期")
    private LocalDateTime payDate;

    @ApiModelProperty(value = "缴费金额")
    private Double noMedicalMoney;

    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "姓名")
    private String paName;

    @ApiModelProperty(value = "科室")
    private String deptName;

    @ApiModelProperty(value = "在院状态")
    private String isHosp;

    @ApiModelProperty(value = "维护医生")
    private String doctor;

    @ApiModelProperty(value = "余额")
    private Double balance;


}
