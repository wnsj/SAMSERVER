package com.jiubo.sam.dao;

import com.jiubo.sam.bean.PatienttypeBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 患者类型 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2019-09-24
 */
public interface PatienttypeDao extends BaseMapper<PatienttypeBean> {

    //查询患者类型
    public List<PatienttypeBean> queryPatientType(PatienttypeBean patienttypeBean);
}
