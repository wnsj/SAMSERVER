package com.jiubo.sam.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiubo.sam.bean.PaPayserviceBean;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author mwl
 * @since 2020-08-10
 */
public interface PaPayserviceDao extends BaseMapper<PaPayserviceBean> {

    //查询需要购买的项目
    List<PaPayserviceBean> queryPaPayService(PaPayserviceBean paPayserviceBean);

    //添加患者缴费项目
    int addPaPayService(PaPayserviceBean paPayserviceBean);

    //添加需要缴费的项目list
    int addAndUpdatePaPayService(List<PaPayserviceBean> list);

    //修改患者缴费项目
    int updatePaPayService(PaPayserviceBean paPayserviceBean);

}
