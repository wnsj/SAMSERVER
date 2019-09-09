package com.jiubo.sam.service;

import com.jiubo.sam.bean.PayserviceBean;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.exception.MessageException;

import java.util.List;

/**
 * <p>
 *  收费项目服务类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface PayserviceService extends IService<PayserviceBean> {

    //查询收费项目
   public List<PayserviceBean> queryPayservice(PayserviceBean payserviceBean) throws MessageException;

   //添加收费项目
    public void addPayservice(PayserviceBean payserviceBean) throws MessageException;

    //修改收费项目
    public void updatePayservice(PayserviceBean payserviceBean) throws MessageException;
}
