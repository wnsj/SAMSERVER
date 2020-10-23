package com.jiubo.sam.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 入院记录表
 * </p>
 *
 * @author dx
 * @since 2020-10-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("Admission_Records")
public class AdmissionRecordsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "AR_ID", type = IdType.AUTO)
    private Integer arId;

    /**
     * 住院号
     */
    private String hospNum;

    /**
     * 是否在院
     */
    private Integer isHos;

    /**
     * 出院时间
     */
    private String arInDate;

    /**
     * 入院时间
     */
    private String arOutDate;

    /**
     * 创建时间
     */
    private String createDate;


}
