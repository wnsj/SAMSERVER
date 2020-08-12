package com.jiubo.sam.service;

import com.jiubo.sam.bean.SupplementaryPaymentBean;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 医疗费补缴服务类
 * </p>
 *
 * @author dx
 * @since 2020-08-11
 */
public interface SupplementaryPaymentService extends IService<SupplementaryPaymentBean> {

    //添加交费记录
    SupplementaryPaymentBean addSupplementaryPayment(SupplementaryPaymentBean supplementaryPaymentBean) throws Exception;

    //修改缴费记录
    SupplementaryPaymentBean updateSupplementaryPayment(SupplementaryPaymentBean supplementaryPaymentBean) throws Exception;

    //查询补缴记录
    List<SupplementaryPaymentBean> querySupplementaryPayment(SupplementaryPaymentBean supplementaryPaymentBean);
}
