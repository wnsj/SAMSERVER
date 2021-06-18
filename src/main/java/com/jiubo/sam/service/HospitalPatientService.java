package com.jiubo.sam.service;

import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.HospitalPatientBean;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.request.HospitalPatientCondition;

public interface HospitalPatientService {
    String addHospitalPatient(HospitalPatientBean hospitalPatientBean) throws Exception;

    PageInfo<HospitalPatientBean> findHospitalPatient(HospitalPatientCondition hospitalPatientBean);

    void updateHospitalPatient(HospitalPatientBean hospitalPatientBean);

    void refundHospitalPatient(HospitalPatientBean hospitalPatientBean) throws MessageException, Exception;
}
