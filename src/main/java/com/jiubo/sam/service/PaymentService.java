package com.jiubo.sam.service;

import com.jiubo.sam.bean.PaymentBean;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  交费 服务类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface PaymentService extends IService<PaymentBean> {

    //添加收费信息
    public int addPayment(PaymentBean paymentBean);
}
