package com.jiubo.sam.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.PaymentDetailsBean;
import com.jiubo.sam.dao.PaymentDetailsDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.request.HospitalPatientCondition;
import com.jiubo.sam.service.PaymentDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentDetailsServiceImpl implements PaymentDetailsService {

    @Autowired
    private PaymentDetailsDao paymentDetailsDao;

    @Override
    public void addPaymentDetails(PaymentDetailsBean paymentDetailsBean) throws MessageException {
        if (paymentDetailsDao.insert(paymentDetailsBean) <= 0) {
            throw new MessageException("添加缴费记录操作失败!");
        }
    }

    @Override
    public Object findPaymentDetail(HospitalPatientCondition hospitalPatientCondition) {
        Integer pageNum = hospitalPatientCondition.getPageNum() == null ? 1:hospitalPatientCondition.getPageNum();
        Integer pageSize = hospitalPatientCondition.getPageSize() == null ? 10:hospitalPatientCondition.getPageSize();
        if(hospitalPatientCondition.getType() == null){
            List<PaymentDetailsBean> list = paymentDetailsDao.findByCondition(hospitalPatientCondition);
            return list;
        }else{
            PageHelper.startPage(pageNum,pageSize);
            List<PaymentDetailsBean> list = paymentDetailsDao.findByCondition(hospitalPatientCondition);
            PageInfo<PaymentDetailsBean> result = new PageInfo<>(list);
            return result;
        }
    }

    @Override
    public Object findPaymentDetailByHos(HospitalPatientCondition hospitalPatientCondition) {
        Integer pageNum = hospitalPatientCondition.getPageNum() == null ? 1:hospitalPatientCondition.getPageNum();
        Integer pageSize = hospitalPatientCondition.getPageSize() == null ? 10:hospitalPatientCondition.getPageSize();
        PageHelper.startPage(pageNum,pageSize);
        List<PaymentDetailsBean> list = paymentDetailsDao.findPaymentDetailByHos(hospitalPatientCondition);
        PageInfo<PaymentDetailsBean> result = new PageInfo<>(list);
        return result;
    }
}
