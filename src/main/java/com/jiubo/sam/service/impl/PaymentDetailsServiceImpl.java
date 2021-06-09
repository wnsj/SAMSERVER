package com.jiubo.sam.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.PaymentDetailsBean;
import com.jiubo.sam.dao.PaymentDetailsDao;
import com.jiubo.sam.dto.PaymentDetailsDto;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.request.HospitalPatientCondition;
import com.jiubo.sam.service.PaymentDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
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
        Double hospitalUseTotal = 0D;//住院发生合计
        Double marginUseTotal = 0D;//预交金缴费合计
        Double patientUseUseTotal = 0D;//门诊发生合计
        List<PaymentDetailsDto> lists = paymentDetailsDao.findByCondition(hospitalPatientCondition);
        for (PaymentDetailsDto PaymentDetailsDto : lists) {
            Double hospitalUse = PaymentDetailsDto.getHospitalUse();
            if (hospitalUse==null){
                hospitalUse=0D;
            }//住院发生合计
            hospitalUseTotal+=hospitalUse;

            Double marginUse = PaymentDetailsDto.getMarginUse();
            if (marginUse==null){
                marginUse=0D;
            }//预交金缴费合计
            marginUseTotal+=marginUse;

            Double patientUse = PaymentDetailsDto.getPatientUse();
            if (patientUse==null){
                patientUse=0D;
            }//门诊发生合计
            patientUseUseTotal+=patientUse;
        }
        Integer pageNum = hospitalPatientCondition.getPageNum() == null ? 1:hospitalPatientCondition.getPageNum();
        Integer pageSize = hospitalPatientCondition.getPageSize() == null ? 10:hospitalPatientCondition.getPageSize();
        //一期先这么改，假如后期需要减一天就注释一下代码，从这行起一直到if以上
        Date endDate = hospitalPatientCondition.getEndDate();
        if (endDate!=null) {
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date time = c.getTime();
            hospitalPatientCondition.setEndDate(time);
        }
        if(hospitalPatientCondition.getType() == null){
            List<PaymentDetailsDto> list = paymentDetailsDao.findByCondition(hospitalPatientCondition);
            for (PaymentDetailsDto paymentDetailsDto : list) {
                paymentDetailsDto.setHospitalUseTotal(hospitalUseTotal);//住院发生合计
                paymentDetailsDto.setMarginUseTotal(marginUseTotal);//预交金缴费合计
                paymentDetailsDto.setPatientUseUseTotal(patientUseUseTotal);//门诊发生合计
            }
            return list;
        }else{
            PageHelper.startPage(pageNum,pageSize);
            List<PaymentDetailsDto> list = paymentDetailsDao.findByCondition(hospitalPatientCondition);
            for (PaymentDetailsDto paymentDetailsDto : list) {
                paymentDetailsDto.setHospitalUseTotal(hospitalUseTotal);//住院发生合计
                paymentDetailsDto.setMarginUseTotal(marginUseTotal);//预交金缴费合计
                paymentDetailsDto.setPatientUseUseTotal(patientUseUseTotal);//门诊发生合计
            }
            PageInfo<PaymentDetailsDto> result = new PageInfo<>(list);
            return result;
        }
    }

    @Override
    public Object findPaymentDetailByHos(HospitalPatientCondition hospitalPatientCondition) {
        Double hospitalUseTotal = 0D;//住院发生合计
        Double marginUseTotal = 0D;//预交金缴费合计
        Double patientUseUseTotal = 0D;//门诊发生合计
        List<PaymentDetailsDto> lists = paymentDetailsDao.findPaymentDetailByHos(hospitalPatientCondition);
        for (PaymentDetailsDto PaymentDetailsDto : lists) {
            Double hospitalUse = PaymentDetailsDto.getHospitalUse();
            if (hospitalUse==null){
                hospitalUse=0D;
            }//住院发生合计
            hospitalUseTotal+=hospitalUse;

            Double marginUse = PaymentDetailsDto.getMarginUse();
            if (marginUse==null){
                marginUse=0D;
            }//预交金缴费合计
            marginUseTotal+=marginUse;

            Double patientUse = PaymentDetailsDto.getPatientUse();
            if (patientUse==null){
                patientUse=0D;
            }//门诊发生合计
            patientUseUseTotal+=patientUse;
        }
        Integer pageNum = hospitalPatientCondition.getPageNum() == null ? 1:hospitalPatientCondition.getPageNum();
        Integer pageSize = hospitalPatientCondition.getPageSize() == null ? 10:hospitalPatientCondition.getPageSize();
        PageHelper.startPage(pageNum,pageSize);
        List<PaymentDetailsDto> list = paymentDetailsDao.findPaymentDetailByHos(hospitalPatientCondition);
        for (PaymentDetailsDto paymentDetailsDto : list) {
            paymentDetailsDto.setHospitalUseTotal(hospitalUseTotal);//住院发生合计
            paymentDetailsDto.setMarginUseTotal(marginUseTotal);//预交金缴费合计
            paymentDetailsDto.setPatientUseUseTotal(patientUseUseTotal);//门诊发生合计
        }
        PageInfo<PaymentDetailsDto> result = new PageInfo<>(list);
        return result;
    }
}
