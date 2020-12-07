package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 * 
 * </p>
 *
 * @author mwl
 * @since 2020-08-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PA_PAYSERVICE")
public class PaPayserviceBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 个人收费项目ID
     */
    @TableId(value = "PP_ID", type = IdType.AUTO)
    private String ppId;

    /**
     * 收费项目ID
     */
    private String payserviceId;

    /**
     * 收费项目名
     */
    @TableField(exist = false)
    private String name;

    /**
     * 页码
     */
    @TableField(exist = false)
    private String pageNum;
    /**
     * 页数
     */
    @TableField(exist = false)
    private String pageSize;
    /**
     * 0:不启用
   1:启用
     */
    private String isUse;

    /**
     * 启用时间
     */
    private String begDate;

    /**
     * 结束时间
     */
    private String endDate;

    /**
     * 患者住院号
     */
    private String hospNum;

    /**
     * 单价
     */
    private String unitPrice;

    /**
     * 预收
     */
    private String preReceive;

    /**
     * 患者ID
     */
    private String patientId;


    /**
     * 项目类型是否计费项目
     */
    @TableField(exist = false)
    private String payType;

    /**
     * 计费天数
//     */
//    @TableField(exist = false)
//    private String days;

    @TableField(exist = false)
    private Integer dayNum;

    @TableField(exist = false)
    private String account;

}
