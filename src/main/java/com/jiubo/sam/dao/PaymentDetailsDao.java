package com.jiubo.sam.dao;

import com.jiubo.sam.bean.HospitalPatientBean;
import com.jiubo.sam.bean.NoMedicalBean;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.bean.PaymentDetailsBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiubo.sam.dto.*;
import com.jiubo.sam.request.HospitalPatientCondition;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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

    MedicalAmount getMedicalAmount(HospitalPatientCondition condition);

    Integer selectByHospNum(@Param("type") Integer type, @Param("now") String now);

    List<PaPayserviceBean> findAllTime(String hospNum);

    List<String> findAllday(String begDatesTime, String endDatesTime);

    List<CheckAccount> getCATable(CACondition condition);

    List<PaymentDetailsBean> getPdByPId(PdCondition condition);

    int deleteAllNoMe();

    List<PaymentDetailsBean> getNewestPDBEveryDay(@Param("idCard") String idCard);

    int addNoMeBatch(List<NoMedicalBean> list);

    List<NoMedicalBean> selectAll();

    Integer selectisHosp(String hospNum);

    List<HospitalPatientBean> findMedicalAmount(HospitalPatientCondition condition);

    PaymentDetailsBean getNextBalance(@Param("hospNum") String hospNum, @Param("payDate") String payDate);

    List<PaymentDetailsBean> getLastAll(@Param("hospNum") String hospNum, @Param("payDate") String payDate);
}
