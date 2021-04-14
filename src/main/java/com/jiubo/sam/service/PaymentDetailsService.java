package com.jiubo.sam.service;

import com.jiubo.sam.bean.PaymentDetailsBean;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.request.HospitalPatientCondition;

public interface PaymentDetailsService {

    void addPaymentDetails(PaymentDetailsBean paymentDetailsBean) throws MessageException;

    Object findPaymentDetail(HospitalPatientCondition hospitalPatientCondition);

    Object findPaymentDetailByHos(HospitalPatientCondition hospitalPatientCondition);
}
