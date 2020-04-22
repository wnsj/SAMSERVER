package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.bean.MedicalExpensesBean;
import com.jiubo.sam.dao.MedicalExpensesDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.MedicalExpensesService;
import com.jiubo.sam.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MedicalExpensesServiceImpl extends ServiceImpl<MedicalExpensesDao, MedicalExpensesBean> implements MedicalExpensesService {


    @Autowired
    private MedicalExpensesDao medicalExpensesDao;

    @Override
    public List<MedicalExpensesBean> queryMedicalExpenses(MedicalExpensesBean medicalExpensesBean) throws MessageException {
        return medicalExpensesDao.queryMedicalExpenses(medicalExpensesBean);
    }

    @Override
    public void addMedicalExpenses(MedicalExpensesBean medicalExpensesBean) throws MessageException {
        String newDate = TimeUtil.getDateYYYY_MM_DD_HH_MM_SS(new Date());
        medicalExpensesBean.setCreateDate(newDate);
        if (medicalExpensesDao.insert(medicalExpensesBean) <= 0) throw new MessageException("操作失败!");
    }

    @Override
    public void updateMedicalExpenses(MedicalExpensesBean medicalExpensesBean) throws MessageException {
        if (medicalExpensesDao.updateById(medicalExpensesBean) <= 0) throw new MessageException("操作失败!");
    }
}
