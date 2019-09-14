package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PatientBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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

    //添加患者信息
    public int addPatient(PatientBean patientBean);

    //插入患者基础信息（有则更新，无则插入）
    public void saveOrUpdate(List<PatientBean> list);

}
