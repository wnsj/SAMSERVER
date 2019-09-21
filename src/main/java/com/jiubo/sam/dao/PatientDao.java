package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PatientBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiubo.sam.bean.PayserviceBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import java.util.List;

/**
 * <p>
 * 患者基础信息 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface PatientDao extends BaseMapper<PatientBean> {


    //患者精确查询
    public List<PatientBean> accurateQuery(PatientBean patientBean);

    //患者模糊查询查询
    public List<PatientBean> fuzzyQuery(PatientBean patientBean);

    //添加患者信息
    public int addPatient(PatientBean patientBean);

    //插入患者基础信息（有则更新，无则插入）
    public void saveOrUpdate(List<PatientBean> list);

    //查询患者信息
    public List<PatientBean> queryPatient(PatientBean patientBean);

}
