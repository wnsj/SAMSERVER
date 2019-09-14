package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PaymentBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

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

    //收费信息汇总查询
    public  List<Map<String, Object>> queryGatherPayment(@Param("sql") String sql);

    //根据患者id查询交费信息
    public List<PaymentBean> queryPaymentByPatientId(String patientId);
}
