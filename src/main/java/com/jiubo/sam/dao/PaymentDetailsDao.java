package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PaymentDetailsBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiubo.sam.dto.MedicalAmount;
import com.jiubo.sam.dto.PaymentDetailsDto;
import com.jiubo.sam.request.HospitalPatientCondition;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    List<PaymentDetailsBean> findByCondition(HospitalPatientCondition hospitalPatientCondition);

    List<PaymentDetailsBean> findPaymentDetailByHos(HospitalPatientCondition hospitalPatientCondition);

    List<PaymentDetailsBean> findPaymentDetailLastByHos(String hospNum);

    MedicalAmount getMedicalAmount();

    Integer selectByHospNum(@Param("type") Integer type,@Param("now") LocalDateTime now);


}
