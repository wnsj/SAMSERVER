package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * @since 2021-04-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PATINET_MARGIN")
@ApiModel(value="PatinetMarginBean对象", description="")
public class PatinetMarginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "患者押金ID")
    @TableId(value = "PM_ID", type = IdType.AUTO)
    private Integer pmId;

    @ApiModelProperty(value = "住院号")
    private String hospNum;

    @ApiModelProperty(value = "押金金额")
    private Double money;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createDate;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime modifyDate;

    @ApiModelProperty(value = "是否删除（1删除2未删除")
    private Integer flag;

    @ApiModelProperty(value = "交押金1退押金2")
    @TableField(exist = false)
    private Integer type;

    @ApiModelProperty(value = "备注")
    @TableField(exist = false)
    private String remark;

    @ApiModelProperty(value = "备注")
    @TableField(exist = false)
    private Integer AccountId;

}
