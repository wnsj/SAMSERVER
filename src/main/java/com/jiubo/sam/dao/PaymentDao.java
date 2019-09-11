package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PaymentBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  交费 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface PaymentDao extends BaseMapper<PaymentBean> {

    //添加交费信息
    public int addPayment(PaymentBean paymentBean);
}
