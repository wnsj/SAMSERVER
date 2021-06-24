package com.jiubo.sam.schedule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.DepartmentBean;
import com.jiubo.sam.bean.EmpDepartmentRefBean;
import com.jiubo.sam.bean.EmployeeBean;
import com.jiubo.sam.bean.HospitalPatientBean;
import com.jiubo.sam.dao.DepartmentDao;
import com.jiubo.sam.dao.EmployeeDao;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.dto.FromHisPatient;
import com.jiubo.sam.service.HospitalPatientService;
import com.jiubo.sam.util.DateUtils;
import com.jiubo.sam.util.WebApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ToHisTask {

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private HospitalPatientService hospitalPatientService;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private EmployeeDao employeeDao;

//    private static final String url = "http://yfzx.bsesoft.com:8002/sjservice.asmx?wsdl";
    private static final String url = "http://192.168.2.79:8081/WebService_Sam_Hospital.asmx?wsdl";

    public void syncPatientAndAddHP() throws Exception {
        Object[] result = requestHis("Z000", "{\"BalanceMoney\": 500}");
        if (result == null) return;
        // 住院余额不足500的集合
        List<HospitalPatientBean> toAddHospitalMoney = new ArrayList<>();
        // 患者信息集合
        List<FromHisPatient> fromHisPatientList = new ArrayList<>();
        for (Object o : result) {
            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            if (!jsonObject.containsKey("item")) continue;
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            if (null == jsonArray || jsonArray.size() <= 0) break;
            for (Object object : jsonArray) {

                JSONObject entity = JSONObject.parseObject(object.toString());
                // 住院号
                String visitSn = entity.getString("VisitSn");
                // 患者姓名
                String patientName = entity.getString("PatientName");
                String sex = entity.getString("Sex");
                String age = entity.getString("Age");
                String idCardNo = entity.getString("IDCardNo");
                // 患者联系方式
                String phoneNo = entity.getString("PhoneNo");
                // 病区编码
                String inPatientAreaNo = entity.getString("InPatientAreaNo");
                // 病区名称
                String inPatientArea = entity.getString("InPatientArea");
                // 科室编码
                Integer departmentNo = entity.getInteger("DepartmentNo");
                // 科室名称
                String department = entity.getString("Department");
                // 床号
                String bedNo = entity.getString("BedNo");
                // 登记日期
                String registerDate = entity.getString("RegisterDate");
                // 住院日期
                String admissionDate = entity.getString("AdmissionDate");
                // 出院日期
                String dischargeDate = entity.getString("DischargeDate");
                // 预交押金
                String totalMoney = entity.getString("TotalMoney");
                // 押金余额
                String balanceMoney = entity.getString("BalanceMoney");

                int k = 0;
                if (sex.equals("男")) {
                    k = 1;
                } else if (sex.equals("女")){
                    k = 2;
                }

                FromHisPatient fromHisPatient = new FromHisPatient();
                fromHisPatient.setAge(age);
                fromHisPatient.setDeptId(departmentNo);
                fromHisPatient.setHospBalance(new BigDecimal(balanceMoney));
                fromHisPatient.setHospNum(visitSn);
                Date hospTime = DateUtils.parseDate(admissionDate);
                fromHisPatient.setHospTime(hospTime);
                if (!StringUtils.isEmpty(dischargeDate)) {
                    Date outHosp = DateUtils.parseDate(dischargeDate);
                    fromHisPatient.setOutHosp(outHosp);
                }
                fromHisPatient.setPatientName(patientName);
                fromHisPatient.setPatientPhone(phoneNo);
                fromHisPatient.setIdCard(idCardNo);
                fromHisPatient.setSex(k);
                fromHisPatientList.add(fromHisPatient);
                if (new BigDecimal("500").compareTo(new BigDecimal(balanceMoney)) >= 0) {
                    HospitalPatientBean hospitalPatientBean = new HospitalPatientBean();
                    Date date = new Date();
                    LocalDateTime dateTime = LocalDateTime.now();
                    hospitalPatientBean.setHospNum(visitSn);
                    hospitalPatientBean.setIdCard(idCardNo);
                    hospitalPatientBean.setAccountId(99999);
                    hospitalPatientBean.setPayDate(date);
                    hospitalPatientBean.setCreateDate(dateTime);
                    hospitalPatientBean.setUpdateDate(date);
                    hospitalPatientBean.setDeptId(departmentNo);
                    hospitalPatientBean.setRealCross(new BigDecimal("3000").doubleValue());
                    // 住院
                    hospitalPatientBean.setType(1);
                    // 缴费
                    hospitalPatientBean.setConsumType(1);
                    hospitalPatientBean.setPayDate(new Date());
                    hospitalPatientBean.setCreateDate(LocalDateTime.now());
                    toAddHospitalMoney.add(hospitalPatientBean);
                }
            }
        }
        // 更新患者信息
        if (!CollectionUtils.isEmpty(fromHisPatientList)) {
            patientDao.updatePatientBatch(fromHisPatientList);
        }


        // 住院费缴费
        if (CollectionUtils.isEmpty(toAddHospitalMoney)) return;
        for (HospitalPatientBean hospitalPatientBean : toAddHospitalMoney) {
            // 维护缴费记录
            hospitalPatientBean.setAccountId(99999);
            String serialNumber = hospitalPatientService.addHospitalPatient(hospitalPatientBean);
            // 充值押金
            toHisAddHP(hospitalPatientBean, serialNumber);
        }

    }

    private void toHisAddHP(HospitalPatientBean hospitalPatientBean, String serialNumber) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("VisitSn", hospitalPatientBean.getHospNum());
        Date date = new Date();
        String formatDate = DateUtils.formatDate(date, "yyyy-MM-dd HH:mm:ss");
        jsonObject.put("TranDate", formatDate);
        jsonObject.put("Cusid", hospitalPatientBean.getIdCard());
        jsonObject.put("TradeNo", serialNumber);
        jsonObject.put("PayType", "9943");
        jsonObject.put("Amt", "3000");

        requestHis("Z003", jsonObject.toJSONString());
    }

    public void syncDept() {
        Object[] result = requestHis("Z032", "{}");
        if (result == null) return;
        List<DepartmentBean> departmentBeanList = new ArrayList<>();
        for (Object o : result) {
            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            if (!jsonObject.containsKey("item")) continue;
            JSONArray jsonArray = jsonObject.getJSONArray("item");
            if (null == jsonArray || jsonArray.size() <= 0) break;
            for (Object object : jsonArray) {
                DepartmentBean departmentBean = new DepartmentBean();
                JSONObject entity = JSONObject.parseObject(object.toString());
                String deptCode = entity.getString("DeptCode");
                String deptName = entity.getString("DeptName");
                Integer isEnabled = entity.getInteger("IsEnabled");
                departmentBean.setName(deptName);
                departmentBean.setDeptId(deptCode);
                departmentBean.setIsuse(String.valueOf(isEnabled));
                departmentBeanList.add(departmentBean);
            }
        }
        if (!CollectionUtils.isEmpty(departmentBeanList)) {
            departmentDao.updateDeptBatch(departmentBeanList);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncEmployee() {
        Object[] result = requestHis("Z042", "{}");
        if (result == null) return;
        List<EmpDepartmentRefBean> refBeanList = new ArrayList<>();
        List<EmployeeBean> employeeBeanList = new ArrayList<>();
        for (Object o : result) {
            JSONObject object = JSONObject.parseObject(o.toString());
            if (!object.containsKey("item")) continue;
            JSONArray jsonArray = object.getJSONArray("item");
            if (null == jsonArray || jsonArray.size() <= 0) break;
            for (Object dto : jsonArray) {
                EmployeeBean employeeBean = new EmployeeBean();
                JSONObject entity = JSONObject.parseObject(dto.toString());
                Long doctorCode = entity.getLong("DoctorCode");
                String doctorName = entity.getString("DoctorName");
                Long deptCode = entity.getLong("DeptCode");
                Integer isEnabled = entity.getInteger("IsEnabled");
                employeeBean.setId(doctorCode);
                employeeBean.setEmpName(doctorName);
                employeeBean.setDeptId(deptCode);
                if (isEnabled == 1) {
                    employeeBean.setFlag(1L);
                } else {
                    employeeBean.setFlag(2L);
                }
                employeeBeanList.add(employeeBean);

                // 医生 科室 关联
                if (null != deptCode) {
                    EmpDepartmentRefBean empDepartmentRefBean = new EmpDepartmentRefBean();
                    empDepartmentRefBean.setEmpId(doctorCode);
                    empDepartmentRefBean.setDeptId(deptCode);
                    empDepartmentRefBean.setCreateDate(new Date());
                    refBeanList.add(empDepartmentRefBean);
                }

            }
        }

        // 备份
        int back = employeeDao.addRefBack();

        // 先删 关联
        employeeDao.deleteAllRef();

        if (!CollectionUtils.isEmpty(employeeBeanList)) {
            employeeDao.updateEmpBatch(employeeBeanList);
        }

        // 建立关联
        if (!CollectionUtils.isEmpty(refBeanList)) {
            employeeDao.insertAll(refBeanList);
        }
    }

    public static Object[] requestHis(String method, String param) {
        Object[] objs = new Object[2];
        objs[0] = method;
        objs[1] = param;
        Object[] result = WebApiUtil.execWebService(url, "CallWebMethod", objs);
        if (null == result || result.length <= 0) {
            return null;
        }
        return result;
    }
}
