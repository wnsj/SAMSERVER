package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.PositionBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author syl
 * @since 2020-08-07
 */
public interface PositionDao extends BaseMapper<PositionBean> {

    List<PositionBean> getPosByCondition(Page page, @Param("positionBean") PositionBean positionBean);

    List<PositionBean> getPosByCondition(@Param("positionBean") PositionBean positionBean);
}
