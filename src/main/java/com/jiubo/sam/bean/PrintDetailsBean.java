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
 * @since 2021-04-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("PRINT_DETAILS")
@ApiModel(value="PrintDetailsBean对象", description="")
public class PrintDetailsBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "打印表主键")
    private Integer printId;

    @ApiModelProperty(value = "数据主键")
    private Integer detailId;

    @ApiModelProperty(value = "今天在此类型的排号")
    private String code;

    @ApiModelProperty(value = "修改时间")
    private LocalDateTime modifyTime;


}
