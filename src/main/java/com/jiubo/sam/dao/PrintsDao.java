package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PrintsBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2021-04-15
 */
public interface PrintsDao extends BaseMapper<PrintsBean> {

    void truncatePrints();

}
