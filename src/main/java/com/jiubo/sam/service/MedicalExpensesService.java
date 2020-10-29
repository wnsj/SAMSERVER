package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.bean.MedicalExpensesBean;
import com.jiubo.sam.exception.MessageException;

import java.text.ParseException;
import java.util.List;

public interface MedicalExpensesService extends IService<MedicalExpensesBean> {

    //查询医疗费信息
    public List<MedicalExpensesBean> queryMedicalExpenses(MedicalExpensesBean medicalExpensesBean) throws Exception;

    //添加医疗费
    public void addMedicalExpenses(MedicalExpensesBean medicalExpensesBean) throws Exception;

    //修改医疗费
    public void updateMedicalExpenses(MedicalExpensesBean medicalExpensesBean) throws Exception;


}
