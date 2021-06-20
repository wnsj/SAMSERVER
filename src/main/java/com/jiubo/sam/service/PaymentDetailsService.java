package com.jiubo.sam.service;

import com.jiubo.sam.bean.PaymentDetailsBean;
import com.jiubo.sam.dto.MedicalAmount;
import com.jiubo.sam.dto.PdCondition;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.request.HospitalPatientCondition;

import java.util.List;

public interface PaymentDetailsService {

    void addPaymentDetails(PaymentDetailsBean paymentDetailsBean) throws MessageException;

    Object findPaymentDetail(HospitalPatientCondition hospitalPatientCondition);

    Object findPaymentDetailByHos(HospitalPatientCondition hospitalPatientCondition);

    MedicalAmount getMedicalAmount();

    List<PaymentDetailsBean> getPdByPId(PdCondition condition);
}
