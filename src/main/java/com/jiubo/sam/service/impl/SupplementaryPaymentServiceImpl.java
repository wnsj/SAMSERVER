package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.MedicalExpensesBean;
import com.jiubo.sam.bean.SupplementaryPaymentBean;
import com.jiubo.sam.dao.MedicalExpensesDao;
import com.jiubo.sam.dao.SupplementaryPaymentDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.SupplementaryPaymentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 医疗费补缴服务实现类
 * </p>
 *
 * @author dx
 * @since 2020-08-11
 */
@Service
public class SupplementaryPaymentServiceImpl extends ServiceImpl<SupplementaryPaymentDao, SupplementaryPaymentBean> implements SupplementaryPaymentService {

    @Autowired
    private SupplementaryPaymentDao supplementaryPaymentDao;

    @Autowired
    private MedicalExpensesDao medicalExpensesDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SupplementaryPaymentBean addSupplementaryPayment(SupplementaryPaymentBean supplementaryPaymentBean) throws Exception {
        Integer meId = supplementaryPaymentBean.getMeId();
        if (meId == null || meId == 0) throw new MessageException("医疗费id为空!");
        MedicalExpensesBean medicalExpensesBean = medicalExpensesDao.selectById(new MedicalExpensesBean().setMeId(String.valueOf(meId)));
        if (medicalExpensesBean == null) throw new MessageException("未查询到该医疗费交费记录!");
        BigDecimal money = Optional.ofNullable(supplementaryPaymentBean.getMoney()).orElse(new BigDecimal(0));
        if (money.compareTo(BigDecimal.ZERO) == 0) throw new MessageException("补缴金额不能为0!");
        BigDecimal realFee = new BigDecimal(medicalExpensesBean.getRealFee());
        if (realFee.compareTo(BigDecimal.ZERO) > 0) throw new MessageException("该医疗费欠款无需补缴!");
        MedicalExpensesBean medical = new MedicalExpensesBean();
        medical.setMeId(String.valueOf(meId));
        //medical.setRealFee(String.valueOf(realFee.add(money)));
        BigDecimal arrearsFee = new BigDecimal(medicalExpensesBean.getArrearsFee());
        medical.setArrearsFee(String.valueOf(arrearsFee.add(money)));
        medicalExpensesDao.updateById(medical);

        QueryWrapper<SupplementaryPaymentBean> wrapper = new QueryWrapper<>();
        wrapper.eq("ME_ID", supplementaryPaymentBean.getMeId());
        List<SupplementaryPaymentBean> supplementaryPaymentBeans = supplementaryPaymentDao.selectList(wrapper);
        supplementaryPaymentBean.setSort(supplementaryPaymentBeans.size() + 1);
        supplementaryPaymentBean.setCreateDate(TimeUtil.getDBTime());
        supplementaryPaymentDao.insert(supplementaryPaymentBean);
        return supplementaryPaymentBean;
    }

    @Override
    public SupplementaryPaymentBean updateSupplementaryPayment(SupplementaryPaymentBean supplementaryPaymentBean) throws Exception {
        if (supplementaryPaymentBean.getSpId() == null || supplementaryPaymentBean.getSpId() == 0)
            throw new MessageException("补缴记录id为空!");
        SupplementaryPaymentBean supplementary = supplementaryPaymentDao.selectById(new SupplementaryPaymentBean().setSpId(supplementaryPaymentBean.getSpId()));
        if (supplementary == null) throw new MessageException("未查询该补缴记录信息!");
        MedicalExpensesBean medicalExpensesBean = medicalExpensesDao.selectById(new MedicalExpensesBean().setMeId(String.valueOf(supplementary.getMeId())));
        if (medicalExpensesBean == null) throw new MessageException("医疗费交费记录错误!");
        BigDecimal realFee = new BigDecimal(medicalExpensesBean.getArrearsFee());
        realFee = realFee.subtract(supplementary.getMoney()).add(supplementaryPaymentBean.getMoney());

        MedicalExpensesBean medical = new MedicalExpensesBean();
        medical.setMeId(medicalExpensesBean.getMeId());
        //medical.setRealFee(String.valueOf(realFee));
        medical.setArrearsFee(String.valueOf(realFee));
        medicalExpensesDao.updateById(medical);
        supplementaryPaymentDao.updateById(supplementaryPaymentBean);
        return supplementaryPaymentBean;
    }

    @Override
    public List<SupplementaryPaymentBean> querySupplementaryPayment(SupplementaryPaymentBean supplementaryPaymentBean) {
        return supplementaryPaymentDao.querySupplementaryPayment(supplementaryPaymentBean);
    }
}
