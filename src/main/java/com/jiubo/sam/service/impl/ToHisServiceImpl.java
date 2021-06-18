package com.jiubo.sam.service.impl;

import cn.hutool.json.JSONObject;
import com.jiubo.sam.bean.HospitalPatientBean;
import com.jiubo.sam.dao.ToHisDao;
import com.jiubo.sam.dto.PatientHiSDto;
import com.jiubo.sam.service.HospitalPatientService;
import com.jiubo.sam.service.ToHisService;
import com.jiubo.sam.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class ToHisServiceImpl implements ToHisService {

    @Autowired
    private ToHisDao toHisDao;

    @Autowired
    private HospitalPatientService hospitalPatientService;

    @Override
    public int addHisEmp(JSONObject jsonObject) {
        String hospNum = jsonObject.getStr("hospNum");
        String name = jsonObject.getStr("name");
        String identityCard = jsonObject.getStr("identityCard");
        String sex = jsonObject.getStr("sex");
        String age = jsonObject.getStr("age");
        String deptId = jsonObject.getStr("deptId");
        String mitypeid = jsonObject.getStr("mitypeid");
        String creator = jsonObject.getStr("creator");

        PatientHiSDto patientHiSDto = PatientHiSDto.builder()
                .hospNum(hospNum).name(name).creator(creator)
                .identityCard(identityCard).sex(sex)
                .age(age).deptId(deptId).mitypeid(mitypeid)
                .build();
        return toHisDao.addHisPatient(patientHiSDto);
    }

    @Override
    public int refundOrAddHP(JSONObject jsonObject) throws Exception {
        String hisLowNum = jsonObject.getStr("hisLowNum");
        String hospNum = jsonObject.getStr("hospNum");
        String identityCard = jsonObject.getStr("identityCard");
        Integer consumType = jsonObject.getInt("consumType");
        String deptId = jsonObject.getStr("deptId");
        Integer empId = jsonObject.getInt("empId");
        String nowDate = jsonObject.getStr("nowDate");
        BigDecimal realCross = jsonObject.getBigDecimal("realCross");
        Integer type = jsonObject.getInt("type");
        HospitalPatientBean hospitalPatientBean = new HospitalPatientBean();
        hospitalPatientBean.setIdCard(identityCard);
        hospitalPatientBean.setSerialNumberHis(hisLowNum);
        hospitalPatientBean.setRealCross(realCross.doubleValue());
        hospitalPatientBean.setHospNum(hospNum);
        Date date = DateUtils.parseDate(nowDate);
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        hospitalPatientBean.setCreateDate(localDateTime);
        // TODO HIS的操作人
        hospitalPatientBean.setAccountId(1);
        hospitalPatientBean.setType(type);
        if (!StringUtils.isEmpty(deptId)) {
            hospitalPatientBean.setDeptId(Integer.parseInt(deptId));
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
}
