package com.jiubo.sam.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.NoMedicalBean;
import com.jiubo.sam.bean.PaPayserviceBean;
import com.jiubo.sam.bean.PaymentDetailsBean;
import com.jiubo.sam.dao.PaPayserviceDao;
import com.jiubo.sam.dao.PaymentDetailsDao;
import com.jiubo.sam.dto.MedicalAmount;
import com.jiubo.sam.dto.PaymentDetailsDto;
import com.jiubo.sam.dto.PdCondition;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.request.HospitalPatientCondition;
import com.jiubo.sam.service.PaymentDetailsService;
import com.jiubo.sam.util.DateUtils;
import com.jiubo.sam.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.neethi.PolicyRegistryImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class PaymentDetailsServiceImpl implements PaymentDetailsService {

    @Autowired
    private PaymentDetailsDao paymentDetailsDao;

    @Autowired
    private PaPayserviceDao paPayserviceDao;


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
        int pageNum = hospitalPatientCondition.getPageNum() == null ? 1 : hospitalPatientCondition.getPageNum();
        int pageSize = hospitalPatientCondition.getPageSize() == null ? 10 : hospitalPatientCondition.getPageSize();

        if (hospitalPatientCondition.getType() == null) {
            List<PaymentDetailsBean> list = paymentDetailsDao.findByCondition(hospitalPatientCondition);
            PageInfo<PaymentDetailsBean> result = new PageInfo<>(list);
            paymentDetailsDto.setList(result);
        } else {
            PageHelper.startPage(pageNum, pageSize);
            List<PaymentDetailsBean> list = paymentDetailsDao.findByCondition(hospitalPatientCondition);
            PageInfo<PaymentDetailsBean> result = new PageInfo<>(list);
            paymentDetailsDto.setList(result);
        }
        return paymentDetailsDto;
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

    @Override
    public List<PaymentDetailsBean> getPdByPId(PdCondition condition) {
        int pageNum = condition.getPageNum() == null ? 1 : condition.getPageNum();
        int pageSize = condition.getPageSize() == null ? 10 : condition.getPageSize();

        // 清空非医疗临时表
        paymentDetailsDao.deleteAllNoMe();

        // 向临时表插入数据
        // 将服务计费表按照查询条件拆分成天
        // 1、确定开始时间 结束时间 同时查出限制条件内计费的项目
        List<NoMedicalBean> pbList = paPayserviceDao.getPBByCondition(condition);
        if (!CollectionUtil.isEmpty(pbList)) {

            // 医疗费明细表每天最新的一条数据
            List<PaymentDetailsBean> paymentDetailsBeanList = paymentDetailsDao.getNewestPDBEveryDay(condition.getIdCard());
            Map<String, List<PaymentDetailsBean>> pdMap = null;
            if (!CollectionUtil.isEmpty(paymentDetailsBeanList)) {
                pdMap = paymentDetailsBeanList
                        .stream()
                        .collect(Collectors.groupingBy(item -> DateUtils.formatDate(item.getPayDate())));
            }

            NoMedicalBean min = pbList.stream().min(Comparator.comparing(NoMedicalBean::getBegDate)).get();
            Date minDate = DateUtils.parseDate(min.getBegDate());
            NoMedicalBean max = pbList.stream().max(Comparator.comparing(NoMedicalBean::getEndDate)).get();
            Date maxDate = DateUtils.parseDate(max.getEndDate());
            Date start = condition.getStartDate() == null ? minDate : condition.getStartDate();
            Date end = condition.getEndDate() == null ? maxDate : condition.getEndDate();
            // 2、根据时间段查出日期列表
            List<String> dateTable = paPayserviceDao.getDateTable(start, end);
            List<NoMedicalBean> countList = new ArrayList<>();
            // 3、同时遍历项目 日期列表 将服务计费表按照查询条件拆分成天
            for (NoMedicalBean noMedicalBean : pbList) {
                Date s = DateUtils.parseDate(noMedicalBean.getBegDate());
                Date ss = DateUtils.parseDate(noMedicalBean.getEndDate());
                for (String df : dateTable) {
                    NoMedicalBean medical = new NoMedicalBean();
                    // 该患者 当天 某个项目的计费实体
                    Date date = DateUtils.parseDate(df);
                    if (date.compareTo(s) < 0 && ss.compareTo(date) < 0)
                        continue;
                    medical.setNoMedicalMoney(noMedicalBean.getUnitPrice());
                    medical.setPayDate(date);
                    medical.setPayDateFormat(df);
                    medical.setIsHosp(noMedicalBean.getIsHosp());
                    medical.setDoctor(noMedicalBean.getDoctor());
                    medical.setDeptName(noMedicalBean.getDeptName());
                    medical.setBegDate(noMedicalBean.getBegDate());
                    medical.setEndDate(noMedicalBean.getEndDate());
                    medical.setDeptId(noMedicalBean.getDeptId());
                    medical.setHospNum(noMedicalBean.getHospNum());
                    medical.setIdCard(noMedicalBean.getIdCard());
                    medical.setPaName(noMedicalBean.getPaName());
                    medical.setUnitPrice(noMedicalBean.getUnitPrice());
                    countList.add(medical);
                }
            }
            // 4、将得到的数据 按缴费日期【天】+ 科室 汇总
            Map<String, List<NoMedicalBean>> noMap = countList.stream().collect(Collectors.groupingBy(item -> item.getPayDateFormat()  + "|" + item.getDeptId()));
            List<NoMedicalBean> resultList = new ArrayList<>();
            Map<String, NoMedicalBean> dayMap = new HashMap<>();
            for (String key : noMap.keySet()) {
                String[] split = key.split("\\|");
                NoMedicalBean result = new NoMedicalBean();
                List<NoMedicalBean> medicalBeans = noMap.get(key);
                NoMedicalBean bean = medicalBeans.get(0);
                BigDecimal sum = medicalBeans.stream().map(NoMedicalBean::getNoMedicalMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
                BeanUtils.copyProperties(bean, result);
                result.setNoMedicalMoney(sum);

                // 没有押金
                if (null == pdMap) {
                    result.setBalance(sum.multiply(new BigDecimal("-1")));
                    resultList.add(result);
                    continue;
                }


                List<PaymentDetailsBean> detailsBeans = pdMap.get(split[0]);

                // 这一天有医疗费记录
                if (!CollectionUtil.isEmpty(detailsBeans)) {
                    arrangeData(resultList, dayMap, split, result, sum, detailsBeans);
                    continue;
                }

                // 若没有医疗费记录
                boolean flag = true;
                Date currentDate = bean.getPayDate();
                String s = DateUtils.formatDate(currentDate);
                int i = 0;
                while (flag) {
                    i++;
                    String formatDate = DateUtils.formatDate(currentDate);
                    if (!dayMap.isEmpty() && dayMap.containsKey(formatDate)) {
                        // 发现前一天有非医疗缴费【先查非医疗缴费原因是 每天的数据 非医疗缴费永远是最后一条，这样算出的余额才是对的】
                        NoMedicalBean medicalBean = dayMap.get(formatDate);
                        BigDecimal balance = medicalBean.getBalance();
                        BigDecimal subtract = balance.subtract(sum);
                        result.setBalance(subtract);
                        result.setIsHosp(medicalBean.getIsHosp());
                        resultList.add(result);
                        dayMap.put(split[0], result);
                        flag = false;
                    } else {
                        List<PaymentDetailsBean> beanList = pdMap.get(formatDate);
                        if (!CollectionUtil.isEmpty(beanList)) {
                            // 没有非医疗缴费 则查前一天的医疗缴费记录
                            arrangeData(resultList, dayMap, split, result, sum, beanList);
                            flag = false;
                        }
                    }
                    int k = dayMap.size() + pdMap.size();
                    if (i==k) {
                        flag = false;
                    }
                    currentDate = TimeUtil.dateAdd(currentDate, TimeUtil.UNIT_DAY, -1);
                    String ss = DateUtils.formatDate(currentDate);
                    log.error(ss);
                }
            }

            if (!CollectionUtil.isEmpty(resultList)) {
                paymentDetailsDao.addNoMeBatch(resultList);
            }
        }


        // 查询明细结果
        PageHelper.startPage(pageNum, pageSize);
        return paymentDetailsDao.getPdByPId(condition);
    }

    private void arrangeData(List<NoMedicalBean> resultList, Map<String, NoMedicalBean> dayMap, String[] split, NoMedicalBean result, BigDecimal sum, List<PaymentDetailsBean> beanList) {
        PaymentDetailsBean detailsBean = beanList.get(0);
        result.setIsHosp(detailsBean.getIsInHospital());
        BigDecimal subtract = new BigDecimal(String.valueOf(detailsBean.getCurrentMargin())).subtract(sum);
        result.setBalance(subtract);
        resultList.add(result);
        dayMap.put(split[0], result);
    }

/*


    String hospNum = hospitalPatientCondition.getHospNum();
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
    }

            //一期先这么改，假如后期需要减一天就注释一下代码，从这行起一直到if以上
        Date endDate = hospitalPatientCondition.getEndDate();
        if (endDate != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(endDate);
            c.add(Calendar.DAY_OF_MONTH, 1);
            Date time = c.getTime();
            hospitalPatientCondition.setEndDate(time);
        }
*/

}
