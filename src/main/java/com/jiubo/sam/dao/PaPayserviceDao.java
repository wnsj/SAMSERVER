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

    //查询需要缴费的项目
    List<PaPayserviceBean> queryPaPayService(PaPayserviceBean paPayserviceBean);

    //添加患者缴费项目
    int addPaPayService(PaPayserviceBean paPayserviceBean);

    //添加需要缴费的项目list
    int addAndUpdatePaPayService(List<PaPayserviceBean> list);

    //修改患者缴费项目
    int updatePaPayService(PaPayserviceBean paPayserviceBean);

    //根据出院-停止所有缴费的项目
    int updatePaPayServiceByPatient(PaPayserviceBean paPayserviceBean);

    //  停止的项目开启
    int updatePaPayServiceById(PaPayserviceBean paPayserviceBean);

}
