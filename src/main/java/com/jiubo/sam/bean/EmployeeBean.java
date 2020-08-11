package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author syl
 * @since 2020-08-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("employee")
public class EmployeeBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 员工姓名
     */
    private String empName;

    /**
     * 性别
     */
    private Long sex;

    /**
     * 政治面貌
     */
    private String politicalStatus;

    /**
     * 出生年月
     */
    private Date birthday;

    /**
     * 联系方式
     */
    private String telPhone;

    /**
     * 籍贯
     */
    private String nativePlace;

    /**
     * 专业
     */
    private String major;

    /**
     * 学历
     */
    private String education;

    /**
     * 毕业院校
     */
    private String graduate;

    /**
     * 岗位
     */
    private Long posId;

    /**
     * 角色
     */
    private Long roleId;

    /**
     * 创建时间
     */
    private Date createDate;

    /**
     * 是否可用（1：可用；2：不可用）
     */
    private Long flag;

    /**
     * 科室id
     */
    @TableField(exist = false)
    private Long deptId;

    @TableField(exist = false)
    private List<String> deptIdList;

    @TableField(exist = false)
    private String deptName;

    @TableField(exist = false)
    private String posName;

    @TableField(exist = false)
    private Long current;

    @TableField(exist = false)
    private Long pageSize;

}
