package com.jiubo.sam.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.bean.PaymentDetailsBean;
import com.jiubo.sam.dao.PaymentDetailsDao;
import com.jiubo.sam.dto.MedicalAmount;
import com.jiubo.sam.dto.PaymentDetailsDto;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.request.HospitalPatientCondition;
import com.jiubo.sam.service.PaymentDetailsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
        PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto();
        Double hospitalUseTotal = 0D;//住院发生合计
        Double marginUseTotal = 0D;//预交金缴费合计
        Double patientUseUseTotal = 0D;//门诊发生合计
        List<PaymentDetailsBean> lists = paymentDetailsDao.findPaymentDetailByHos(hospitalPatientCondition);
        for (PaymentDetailsBean paymentDetailsBean : lists) {
            Double hospitalUse = paymentDetailsBean.getHospitalUse();
            if (hospitalUse == null) {
                hospitalUse = 0D;
            }//住院发生合计
            hospitalUseTotal += hospitalUse;

            Double marginUse = paymentDetailsBean.getMarginUse();
            if (marginUse == null) {
                marginUse = 0D;
            }//预交金缴费合计
            marginUseTotal += marginUse;

            Double patientUse = paymentDetailsBean.getPatientUse();
            if (patientUse == null) {
                patientUse = 0D;
            }//门诊发生合计
            patientUseUseTotal += patientUse;
        }
        hospitalUseTotal = (double) Math.round(hospitalUseTotal * 100) / 100;
        marginUseTotal = (double) Math.round(marginUseTotal * 100) / 100;
        patientUseUseTotal = (double) Math.round(patientUseUseTotal * 100) / 100;
        paymentDetailsDto.setHospitalUseTotal(hospitalUseTotal);
        paymentDetailsDto.setMarginUseTotal(marginUseTotal);
        paymentDetailsDto.setPatientUseUseTotal(patientUseUseTotal);
        Integer pageNum = hospitalPatientCondition.getPageNum() == null ? 1 : hospitalPatientCondition.getPageNum();
        Integer pageSize = hospitalPatientCondition.getPageSize() == null ? 10 : hospitalPatientCondition.getPageSize();
        //一期先这么改，假如后期需要减一天就注释一下代码，从这行起一直到if以上
        Date endDate = hospitalPatientCondition.getEndDate();
        if (endDate != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date time = c.getTime();
            hospitalPatientCondition.setEndDate(time);
        }
        if (hospitalPatientCondition.getType() == null) {
            List<PaymentDetailsBean> list = paymentDetailsDao.findByCondition(hospitalPatientCondition);



            /*String hospNum = hospitalPatientCondition.getHospNum();
            List<PaPayserviceBean> paPayserviceBeans = paymentDetailsDao.findAllTime(hospNum);
            //Map<String, List<PaPayserviceBean>> collect = paPayserviceBeans.stream().collect(Collectors.groupingBy(PaPayserviceBean::getHospNum));

            Map<String, List<BigDecimal>> map = new HashMap<>();
            for (PaPayserviceBean paPayserviceBean : paPayserviceBeans) {
                String preReceive = paPayserviceBean.getPreReceive();
                BigDecimal b =new BigDecimal(preReceive);

                String begDates = paPayserviceBean.getBegDate();
                String endDates = paPayserviceBean.getEndDate();
                List<String> allday = paymentDetailsDao.findAllday(begDates, endDates);
                BigDecimal number = new BigDecimal(0);
                int value=allday.size();
                number=BigDecimal.valueOf((int)value);
                BigDecimal result5 = b.divide(number,2,BigDecimal.ROUND_HALF_UP);
                for (String day : allday) {
                    if (map.containsKey(day)) {//map有这一天
                        List<BigDecimal> integers = map.get(day);
                        integers.add(result5);
                    } else {
                        List<BigDecimal> listInt = new ArrayList<>();
                        listInt.add(result5);
                        map.put(day, listInt);
                    }
                }
            }
            //至此把时间跟余额集合存到了map里面
            //放完map
            for (PaymentDetailsBean paymentDetailsBean : list) {
                Integer pdId = paymentDetailsBean.getPdId();
                String patientName = paymentDetailsBean.getPatientName();
                String deptName = paymentDetailsBean.getDeptName();
                Integer isInHospital = paymentDetailsBean.getIsInHospital();
            }
            for (String key : map.keySet()) {//keySet获取map集合key的集合  然后在遍历key即可
                for (PaPayserviceBean paPayserviceBean : paPayserviceBeans) {
                    List<BigDecimal> integers = map.get(key);
                    BigDecimal number = new BigDecimal(0);
                    for (BigDecimal integer : integers) {
                        number = integer.add(number);
                    }
                    PaymentDetailsBean paymentDetailsBean = new PaymentDetailsBean();
                    paymentDetailsBean.setNoMeUse(number);
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime ldt = LocalDateTime.parse(key,df);
                    paymentDetailsBean.setCreateDate(ldt);
                    paymentDetailsBean.setHospNum(hospNum);
                }
            }*/



            PageInfo<PaymentDetailsBean> result = new PageInfo<>(list);
            paymentDetailsDto.setList(result);
            return paymentDetailsDto;
        } else {
            PageHelper.startPage(pageNum, pageSize);
            List<PaymentDetailsBean> list = paymentDetailsDao.findByCondition(hospitalPatientCondition);
            PageInfo<PaymentDetailsBean> result = new PageInfo<>(list);
            paymentDetailsDto.setList(result);
            return paymentDetailsDto;
        }
    }

    @Override
    public Object findPaymentDetailByHos(HospitalPatientCondition hospitalPatientCondition) {
        PaymentDetailsDto paymentDetailsDto = new PaymentDetailsDto();
        Double hospitalUseTotal = 0D;//住院发生合计
        Double marginUseTotal = 0D;//预交金缴费合计
        Double patientUseUseTotal = 0D;//门诊发生合计
        Double marginAmountTotal = 0D;//余额合计
        List<PaymentDetailsBean> lists = paymentDetailsDao.findPaymentDetailByHos(hospitalPatientCondition);
        for (PaymentDetailsBean paymentDetailsBean : lists) {
            Double hospitalUse = paymentDetailsBean.getHospitalUse();
            if (hospitalUse == null) {
                hospitalUse = 0D;
            }//住院发生合计
            hospitalUseTotal += hospitalUse;

            Double marginUse = paymentDetailsBean.getMarginUse();
            if (marginUse == null) {
                marginUse = 0D;
            }//预交金缴费合计
            marginUseTotal += marginUse;

            Double patientUse = paymentDetailsBean.getPatientUse();
            if (patientUse == null) {
                patientUse = 0D;
            }//门诊发生合计
            patientUseUseTotal += patientUse;

            double marginAmount = 0D;
            String amount = paymentDetailsBean.getMarginAmount();
            if (!StringUtils.isEmpty(amount)) {
                marginAmount = Double.parseDouble(amount);
            }

            //余额合计
            marginAmountTotal += marginAmount;

        }
        marginAmountTotal = (double) Math.round(marginAmountTotal * 100) / 100;
        hospitalUseTotal = (double) Math.round(hospitalUseTotal * 100) / 100;
        marginUseTotal = (double) Math.round(marginUseTotal * 100) / 100;
        patientUseUseTotal = (double) Math.round(patientUseUseTotal * 100) / 100;

        paymentDetailsDto.setHospitalUseTotal(hospitalUseTotal);
        paymentDetailsDto.setMarginUseTotal(marginUseTotal);
        paymentDetailsDto.setPatientUseUseTotal(patientUseUseTotal);
        paymentDetailsDto.setMarginAmountUseTotal(marginAmountTotal);
        Integer pageNum = hospitalPatientCondition.getPageNum() == null ? 1 : hospitalPatientCondition.getPageNum();
        Integer pageSize = hospitalPatientCondition.getPageSize() == null ? 10 : hospitalPatientCondition.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        List<PaymentDetailsBean> list = paymentDetailsDao.findPaymentDetailByHos(hospitalPatientCondition);
        PageInfo<PaymentDetailsBean> result = new PageInfo<>(list);
        paymentDetailsDto.setList(result);
        return paymentDetailsDto;
    }

    @Override
    public MedicalAmount getMedicalAmount() {
        return paymentDetailsDao.getMedicalAmount();
    }
}
