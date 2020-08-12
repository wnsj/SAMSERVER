package com.jiubo.sam.dao;

import com.jiubo.sam.bean.SupplementaryPaymentBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 医疗费补缴 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2020-08-11
 */
public interface SupplementaryPaymentDao extends BaseMapper<SupplementaryPaymentBean> {

    //查询医疗费补缴记录
    public List<SupplementaryPaymentBean> querySupplementaryPayment(SupplementaryPaymentBean supplementaryPaymentBean);
}
