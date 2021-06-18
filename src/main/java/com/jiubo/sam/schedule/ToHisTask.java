package com.jiubo.sam.schedule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.bean.HospitalPatientBean;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.dto.FromHisPatient;
import com.jiubo.sam.service.HospitalPatientService;
import com.jiubo.sam.util.DateUtils;
import com.jiubo.sam.util.WebApiUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class ToHisTask {

    @Autowired
    private PatientDao patientDao;

    @Autowired
    private HospitalPatientService hospitalPatientService;

    private static final String url = "http://yfzx.bsesoft.com:8002/sjservice.asmx?wsdl";

    public void getAllPatient() throws Exception {
        String method = "Z000";
        String sendJson = "{\"BalanceMoney\": 500}";
        Object[] objs = new Object[2];
        objs[0] = method;
        objs[1] = sendJson;
        Object[] result = WebApiUtil.execWebsevice(url, "CallWebMethod", objs);
        String string = Arrays.toString(result);
        if (null == result || result.length <= 0) {
            return;
        }
        // 住院余额不足500的集合
        List<HospitalPatientBean> toAddHospitalMoney = new ArrayList<>();
        // 患者信息集合
        List<FromHisPatient> fromHisPatientList = new ArrayList<>();
        for (Object o : result) {
            JSONObject jsonObject = JSONObject.parseObject(o.toString());
            if (!jsonObject.containsKey("item")) continue;
            JSONArray jsonArray = jsonObject.getJSONArray("item");

            for (Object object : jsonArray) {

                JSONObject entity = JSONObject.parseObject(object.toString());
                // 住院号
                String visitSn = entity.getString("VisitSn");
                // 患者姓名
                String patientName = entity.getString("PatientName");
                Integer sex = entity.getInteger("Sex");
                Integer age = entity.getInteger("Age");
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
                fromHisPatient.setSex(sex);
                fromHisPatientList.add(fromHisPatient);
                if (new BigDecimal("500").compareTo(new BigDecimal(balanceMoney)) >= 0) {
                    HospitalPatientBean hospitalPatientBean = new HospitalPatientBean();
                    hospitalPatientBean.setHospNum(visitSn);
                    hospitalPatientBean.setIdCard(idCardNo);
                    hospitalPatientBean.setDeptId(departmentNo);
                    hospitalPatientBean.setRealCross(new BigDecimal("3000").doubleValue());
                    hospitalPatientBean.setAccountId(0);
                    // 住院
                    hospitalPatientBean.setType(1);
                    // 缴费
                    hospitalPatientBean.setConsumType(1);
                    toAddHospitalMoney.add(hospitalPatientBean);
                }
            }
        }
        // 更新患者信息
        patientDao.updatePatientBatch(fromHisPatientList);

        // 住院费缴费
        for (HospitalPatientBean hospitalPatientBean : toAddHospitalMoney) {
            // 维护缴费记录
            String serialNumber = hospitalPatientService.addHospitalPatient(hospitalPatientBean);
            // 充值押金
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("VisitSn",hospitalPatientBean.getHospNum());
            Date date = new Date();
            String formatDate = DateUtils.formatDate(date, "yyyy-MM-dd HH:mm:ss");
            jsonObject.put("TranDate",formatDate);
            jsonObject.put("Cusid",hospitalPatientBean.getIdCard());
            jsonObject.put("TradeNo",serialNumber);
            jsonObject.put("PayType","9943");
            jsonObject.put("Amt","3000");
            String payMethod = "Z003";
            Object[] params = new Object[2];
            params[0] = payMethod;
            params[1] = jsonObject.toJSONString();
            WebApiUtil.execWebsevice(url, "CallWebMethod", params);
        }
    }
}
