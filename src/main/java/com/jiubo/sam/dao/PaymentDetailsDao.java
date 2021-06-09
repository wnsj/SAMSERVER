package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PaymentDetailsBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiubo.sam.dto.PaymentDetailsDto;
import com.jiubo.sam.request.HospitalPatientCondition;

import java.util.List;

/**
 * <p>
 * 缴费明细 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2021-04-10
 */
public interface PaymentDetailsDao extends BaseMapper<PaymentDetailsBean> {

    List<PaymentDetailsDto> findByCondition(HospitalPatientCondition hospitalPatientCondition);

    List<PaymentDetailsDto> findPaymentDetailByHos(HospitalPatientCondition hospitalPatientCondition);
}
