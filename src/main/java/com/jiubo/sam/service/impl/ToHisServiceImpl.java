package com.jiubo.sam.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.jiubo.sam.bean.EmployeeBean;
import com.jiubo.sam.bean.HospitalPatientBean;
import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.dao.EmployeeDao;
import com.jiubo.sam.dao.PaymentDetailsDao;
import com.jiubo.sam.dao.ToHisDao;
import com.jiubo.sam.dto.CACondition;
import com.jiubo.sam.dto.CheckAccount;
import com.jiubo.sam.dto.PatientHiSDto;
import com.jiubo.sam.dto.PayServiceDto;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.schedule.ToHisTask;
import com.jiubo.sam.service.HospitalPatientService;
import com.jiubo.sam.service.ToHisService;
import com.jiubo.sam.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;

import java.time.LocalDateTime;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int addHisEmp(String param) throws MessageException {
        JSONObject jsonObject = JSONObject.parseObject(param);

        String hospNum = jsonObject.getString("hospNum");
        String name = jsonObject.getString("name");
        String identityCard = jsonObject.getString("identityCard");
        String sex = jsonObject.getString("sex");
        String age = jsonObject.getString("age");
        String deptId = jsonObject.getString("deptId");
        String mitypeid = jsonObject.getString("mitypeid");
//        String creator = jsonObject.getString("creator");

        // 判空
        judgeEmpty(hospNum, name, identityCard, deptId, mitypeid);


        PatientHiSDto patientHiSDto = PatientHiSDto.builder()
                .hospNum(hospNum).name(name).creator(99999)
                .identityCard(identityCard).sex(sex)
                .age(age).deptId(deptId).mitypeid(mitypeid)
                .build();
        return toHisDao.addHisPatient(patientHiSDto);
    }

    private void judgeEmpty(String hospNum, String name, String identityCard, String deptId, String mitypeid) throws MessageException {
        if (StringUtils.isEmpty(hospNum)) {
            throw new MessageException("住院号不可为空");
        }
        if (StringUtils.isEmpty(name)) {
            throw new MessageException("患者名字不可为空");
        }
        if (StringUtils.isEmpty(identityCard)) {
            throw new MessageException("身份证号不可为空");
        }
        if (StringUtils.isEmpty(deptId)) {
            throw new MessageException("科室id不可为空");
        }
        if (StringUtils.isEmpty(mitypeid)) {
            throw new MessageException("医保类型不可为空");
        }
//        if (StringUtils.isEmpty(creator)){
//            throw new MessageException("操作人不可为空");
//        }
    }

    @Transactional(rollbackFor = Exception.class)
    public JSONObject refundOrAddHP(String param) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(param);
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
        hospitalPatientBean.setAccountId(99999);
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
        LocalDateTime dateTime = LocalDateTime.now();
        hospitalPatientBean.setCreateDate(dateTime);
        String serialNumber;
        if (consumType == 1) {
            serialNumber = hospitalPatientService.addHospitalPatient(hospitalPatientBean);
        } else {
            serialNumber = hospitalPatientService.refundHospitalPatient(hospitalPatientBean);
        }
        JSONObject returnData = new JSONObject();
        returnData.put("samLowNum",serialNumber);
        returnData.put("hisLowNum",hisLowNum);
        return returnData;
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
        if (null == type) {
            throw new MessageException("缴费类型不能为空");
        }
    }

    @Override
    public List<CheckAccount> getCATable(CACondition condition) {
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
            hisDataMap = getHisData(start, end);
        } catch (Exception e) {
            log.error("获取HIS数据异常");
        }

        // 医生数据
        List<EmployeeBean> employeeBeanList = employeeDao.getAllEmp();
        Map<Long, List<EmployeeBean>> empMap = null;
        if (!CollectionUtil.isEmpty(employeeBeanList)) {
            empMap = employeeBeanList.stream().collect(Collectors.groupingBy(EmployeeBean::getId));
        }

        if (CollectionUtil.isEmpty(caTable)) return new ArrayList<>();

        // 整合圣安 HIS 数据【目前根据 身份证号+HIS的流水号进行匹配】
        for (CheckAccount checkAccount : caTable) {
            if (hisDataMap.isEmpty()) continue;
            JSONObject hisObj = hisDataMap.get(checkAccount.getSamIdCard() + "|" + checkAccount.getSamSerialNumberSam());
            if (null == hisObj) continue;
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
        return caTable;
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
                Object kh = entity.get("Kh");
                Object tradeno = entity.get("TradeNo");
                String key = kh + "|" + tradeno;
                hisDataMap.put(key, entity);
            }
        }

        return hisDataMap;
    }
}
