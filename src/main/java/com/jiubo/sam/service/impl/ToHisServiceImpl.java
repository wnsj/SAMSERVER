package com.jiubo.sam.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiubo.sam.bean.EmployeeBean;
import com.jiubo.sam.bean.HospitalPatientBean;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.dao.EmployeeDao;
import com.jiubo.sam.dao.PaymentDetailsDao;
import com.jiubo.sam.dao.ToHisDao;
import com.jiubo.sam.dto.*;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.schedule.ToHisTask;
import com.jiubo.sam.service.HospitalPatientService;
import com.jiubo.sam.service.ToHisService;
import com.jiubo.sam.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ToHisServiceImpl implements ToHisService {

    @Autowired
    private ToHisDao toHisDao;

    @Autowired
    private HospitalPatientService hospitalPatientService;

    @Autowired
    private PaymentDetailsDao paymentDetailsDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public int addHisEmp(JSONObject jsonObject) throws MessageException {
        String hospNum = jsonObject.getString("hospNum");
        String name = jsonObject.getString("name");
        String identityCard = jsonObject.getString("identityCard");
        String sex = jsonObject.getString("sex");
        String age = jsonObject.getString("age");
        String deptId = jsonObject.getString("deptId");
        String mitypeid = jsonObject.getString("mitypeid");
        String creator = jsonObject.getString("creator");

        // 判空
        judgeEmpty(hospNum, name, identityCard, deptId, mitypeid);


        PatientHiSDto patientHiSDto = PatientHiSDto.builder()
                .hospNum(hospNum).name(name).creator(creator)
                .identityCard(identityCard).sex(sex)
                .age(age).deptId(deptId).mitypeid(mitypeid)
                .build();
        return toHisDao.addHisPatient(patientHiSDto);
    }

    private void judgeEmpty(String hospNum, String name, String identityCard, String deptId, String mitypeid) throws MessageException {
        if (StringUtils.isEmpty(hospNum)){
            throw new MessageException("住院号不可为空");
        }
        if (StringUtils.isEmpty(name)){
            throw new MessageException("患者名字不可为空");
        }
        if (StringUtils.isEmpty(identityCard)){
            throw new MessageException("身份证号不可为空");
        }
        if (StringUtils.isEmpty(deptId)){
            throw new MessageException("科室id不可为空");
        }
        if (StringUtils.isEmpty(mitypeid)){
            throw new MessageException("医保类型不可为空");
        }
//        if (StringUtils.isEmpty(creator)){
//            throw new MessageException("操作人不可为空");
//        }
    }

    public int refundOrAddHP(JSONObject jsonObject) throws Exception {
        String hisLowNum = jsonObject.getString("hisLowNum");
        String hospNum = jsonObject.getString("hospNum");
        String identityCard = jsonObject.getString("identityCard");
        Integer consumType = jsonObject.getInteger("consumType");
        String deptId = jsonObject.getString("deptId");
        Integer empId = jsonObject.getInteger("empId");
        String nowDate = jsonObject.getString("nowDate");
        BigDecimal realCross = jsonObject.getBigDecimal("realCross");
        Integer type = jsonObject.getInteger("type");

        // 对必填字段判空
        judgeIsEmpty(hospNum, identityCard, consumType, nowDate, realCross, type);

        List<PatientBean> patientBeans = toHisDao.accurateQuery(identityCard);
        PatientBean patientBean = null;
        if (!CollectionUtil.isEmpty(patientBeans)) {
            patientBean = patientBeans.get(0);
        }
        HospitalPatientBean hospitalPatientBean = new HospitalPatientBean();
        hospitalPatientBean.setIdCard(identityCard);
        hospitalPatientBean.setSerialNumberHis(hisLowNum);
        hospitalPatientBean.setRealCross(realCross.doubleValue());
        hospitalPatientBean.setHospNum(hospNum);
        Date date = DateUtils.parseDate(nowDate);
//        Instant instant = date.toInstant();
//        ZoneId zoneId = ZoneId.systemDefault();
//        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        hospitalPatientBean.setPayDate(date);
        // TODO HIS的操作人
        hospitalPatientBean.setAccountId(1);
        hospitalPatientBean.setType(type);
        if (!StringUtils.isEmpty(deptId)) {
            hospitalPatientBean.setDeptId(Integer.parseInt(deptId));
        } else {
            if (null != patientBean) {
                hospitalPatientBean.setDeptId(Integer.parseInt(patientBean.getDeptId()));
            }
        }
        if (null != empId) {
            hospitalPatientBean.setEmpId(empId);
        }
        if (consumType == 1) {
            hospitalPatientService.addHospitalPatient(hospitalPatientBean);
        } else {
            hospitalPatientService.refundHospitalPatient(hospitalPatientBean);
        }
        return 0;
    }

    private void judgeIsEmpty(String hospNum, String identityCard, Integer consumType, String nowDate, BigDecimal realCross, Integer type) throws MessageException {
        if (StringUtils.isEmpty(hospNum)) {
            throw new MessageException("住院号不能为空");
        }
        if (StringUtils.isEmpty(identityCard)) {
            throw new MessageException("身份证号不能为空");
        }
        if (null == consumType) {
            throw new MessageException("缴退类型不能为空");
        }
        if (StringUtils.isEmpty(nowDate)) {
            throw new MessageException("交易时间不能为空");
        }
        if (null == realCross || BigDecimal.ZERO.compareTo(realCross) >= 0) {
            throw new MessageException("发生金额不能为空");
        }
        if (null != type) {
            throw new MessageException("缴费类型不能为空");
        }
    }

    @Override
    public CaTableDto getCATable(CACondition condition) {
        Date startDate = condition.getStartDate();
        Date endDate = condition.getEndDate();
        String start = DateUtils.formatDate(startDate, "yyyy-MM-dd HH:mm:ss");
        String end = DateUtils.formatDate(endDate, "yyyy-MM-dd HH:mm:ss");
        int pageNum = condition.getPageNum() == null ? 1 : condition.getPageNum();
        int pageSize = condition.getPageSize() == null ? 10 : condition.getPageSize();
        PageHelper.startPage(pageNum, pageSize);
        // 圣安数据
        List<CheckAccount> caTable = paymentDetailsDao.getCATable(condition);
        // 获取his数据
        Map<String, JSONObject> hisDataMap = new HashMap<>();
        try {
            hisDataMap =  getHisData(start, end);
        } catch (Exception e) {
            log.error("获取HIS数据异常");
        }

        // 医生数据
        List<EmployeeBean> employeeBeanList = employeeDao.getAllEmp();
        Map<Long, List<EmployeeBean>> empMap = null;
        if (!CollectionUtil.isEmpty(employeeBeanList)) {
            empMap = employeeBeanList.stream().collect(Collectors.groupingBy(EmployeeBean::getId));
        }

        if (CollectionUtil.isEmpty(caTable)) return null;

        // 整合圣安 HIS 数据【目前根据 身份证号+HIS的流水号进行匹配】
        for (CheckAccount checkAccount : caTable) {
            if (hisDataMap.isEmpty()) continue;
            JSONObject hisObj = hisDataMap.get(checkAccount.getSamIdCard() + "|" + checkAccount.getSamSerialNumberHis());
            checkAccount.setHisCharge(hisObj.getBigDecimal("ysje"));
            checkAccount.setHisDeveloper(hisObj.getString("port"));
            checkAccount.setHisIdCard(hisObj.getString("Kh"));
            checkAccount.setHisPayMethod(hisObj.getString("PayType"));
            checkAccount.setHisPayType(hisObj.getInteger("zfzt"));
            checkAccount.setHisRefund(hisObj.getBigDecimal("tfje"));
            checkAccount.setHisSerialNumberHis(hisObj.getString("TradeNo"));
            checkAccount.setHisTradeDate(hisObj.getString("jyrq"));
            if (null == empMap) continue;
            List<EmployeeBean> employeeBeanList1 = empMap.get(hisObj.getLong("czyh"));
            if (!CollectionUtil.isEmpty(employeeBeanList1)) {
                checkAccount.setHisOperator(employeeBeanList1.get(0).getEmpName());
            }
        }

        //加上缴费，退费总计
        BigDecimal samChargeMax = new BigDecimal("0");//圣安缴费金额
        BigDecimal samRefundMax = new BigDecimal("0");//圣安退费金额
        BigDecimal hisChargeMax = new BigDecimal("0");//HIS缴费金额
        BigDecimal hisRefundMax = new BigDecimal("0");//HIS退费金额

        for (CheckAccount checkAccount : caTable) {
            BigDecimal samCharge = checkAccount.getSamCharge();
            BigDecimal samRefund = checkAccount.getSamRefund();
            BigDecimal hisCharge = checkAccount.getHisCharge();
            BigDecimal hisRefund = checkAccount.getHisRefund();
            samChargeMax.add(samCharge);
            samRefundMax.add(samRefund);
            hisChargeMax.add(hisCharge);
            hisRefundMax.add(hisRefund);
        }
        PageInfo<CheckAccount> result = new PageInfo<>(caTable);

        CaTableDto caTableDto = new CaTableDto();
        caTableDto.setList(result);
        caTableDto.setSamChargeMax(samChargeMax);
        caTableDto.setSamRefundMax(samRefundMax);
        caTableDto.setHisChargeMax(hisChargeMax);
        caTableDto.setHisRefundMax(hisRefundMax);
        return caTableDto;
    }

    // 请求his获取交易数据
    private Map<String, JSONObject> getHisData(String start, String end) {
        com.alibaba.fastjson.JSONObject object = new com.alibaba.fastjson.JSONObject();
        object.put("billStartDate", start);
        object.put("billEndDate", end);
        Object[] objects = ToHisTask.requestHis("Z050", object.toJSONString());
        Map<String, JSONObject> hisDataMap = new HashMap<>();
        if (null == objects || objects.length <= 0) return new HashMap<>();
        for (Object o : objects) {

            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            if (!jsonObject.containsKey("item")) continue;

            JSONArray jsonArray = jsonObject.getJSONArray("item");
            if (null == jsonArray || jsonArray.size() <= 0) break;

            for (Object obj : jsonArray) {
                JSONObject entity = JSONObject.parseObject(obj.toString());
                String kh = entity.getString("Kh");
                String tradeNo = entity.getString("tradeNo");
                String key = kh + "|" + tradeNo;
                hisDataMap.put(key, entity);
            }
        }

        return hisDataMap;
    }
}
