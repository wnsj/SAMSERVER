package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 科室Bean
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("DEPARTMENT")
public class DepartmentBean implements Serializable {

    private static final long serialVersionUID = 1L;

    //科室id
    @TableId(value = "DEPT_ID", type = IdType.AUTO)
    private String deptId;

    //科室名
    @ApiModelProperty(value = "科室名")
    private String name;

    //是否启用【1:启用，0:不启用】
    @ApiModelProperty(value = "是否启用【1:启用，0:不启用】")
    private String isuse;

    //是否启动 【1:启用，0或者空:不启用】
    @ApiModelProperty(value = "是否启动 【1:启用，0或者空:不启用】")
    private String isStart;

    //欠费
    @ApiModelProperty(value = "欠费")
    @TableField(exist = false)
    private String arrease;

    //即将欠费
    @ApiModelProperty(value = "即将欠费")
    @TableField(exist = false)
    private String preArrease;

    //部门LIST_ID
    @ApiModelProperty(value = "部门LIST_ID")
    @TableField(exist = false)
    private List<String> deptList;

    @TableField(exist = false)
    private String accountId;

}
