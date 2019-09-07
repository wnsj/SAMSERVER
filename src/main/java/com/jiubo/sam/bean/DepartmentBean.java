package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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

    @TableId(value = "DEPT_ID", type = IdType.AUTO)
    private Integer deptId;

    private String name;

    /**
     * 0:启用
   1:不启用
     */
    private Integer isuse;


}
