package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.PositionBean;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author syl
 * @since 2020-08-07
 */
public interface PositionService extends IService<PositionBean> {
    Page<PositionBean> getPosByCondition(PositionBean positionBean);

    void addPos(PositionBean positionBean);

    void patchPos(PositionBean positionBean);

    List<PositionBean> getAllPos(PositionBean positionBean);
}
