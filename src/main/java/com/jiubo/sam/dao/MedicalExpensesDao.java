package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiubo.sam.bean.MedicalExpensesBean;

import java.util.List;

public interface MedicalExpensesDao  extends BaseMapper<MedicalExpensesBean> {

    public List<MedicalExpensesBean> queryMedicalExpenses(MedicalExpensesBean medicalExpensesBean);
}
