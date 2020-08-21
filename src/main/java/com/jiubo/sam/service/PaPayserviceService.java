package com.jiubo.sam.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.bean.PaPayserviceBean;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author mwl
 * @since 2020-08-10
 */
public interface PaPayserviceService extends IService<PaPayserviceBean> {

    //查询需要购买的项目
    List<PaPayserviceBean> queryPaPayService(PaPayserviceBean paPayserviceBean) throws Exception;


    //修改患者缴费项目
    public void updatePaPayService(PaPayserviceBean paPayserviceBean) throws  Exception;


//    //添加单个患者缴费项目
//    public void addPaPayService(PaPayserviceBean paPayserviceBean) throws  Exception;


    //添加和修改患者缴费项目LIST
    public void addAndUpdatePaPayService(List<PaPayserviceBean> list) throws  Exception;

    //添加和修改患者缴费项目单条
    public PaPayserviceBean addAndUpdatePps(PaPayserviceBean paPayserviceBean) throws Exception;


}
