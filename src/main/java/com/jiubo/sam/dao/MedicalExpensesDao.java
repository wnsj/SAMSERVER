package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.MedicalExpensesBean;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.bean.PaymentBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MedicalExpensesDao extends BaseMapper<MedicalExpensesBean> {

    public List<MedicalExpensesBean> queryMedicalExpenses(MedicalExpensesBean medicalExpensesBean);

    List<PaymentBean> getMedicalDetails(PaymentBean paymentBean);

    List<MedicalExpensesBean> getMeByHospNums(@Param("patientBean") PatientBean patientBean);

    List<MedicalExpensesBean> getMeByHospNumsPage(List<String> numList);
}
