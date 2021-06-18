package com.jiubo.sam.service.impl;

import cn.hutool.json.JSONObject;
import com.jiubo.sam.dao.ToHisDao;
import com.jiubo.sam.dto.PatientHiSDto;
import com.jiubo.sam.dto.PayServiceDto;
import com.jiubo.sam.service.HospitalPatientService;
import com.jiubo.sam.service.ToHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public void refundHP(JSONObject jsonObject) {
//        hospitalPatientService.refundHospitalPatient();
    }
}
