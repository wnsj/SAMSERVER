package com.jiubo.sam.service;

import com.jiubo.sam.bean.PatienttypeBean;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.exception.MessageException;

import java.util.List;

/**
 * <p>
 *  患者类型服务类
 * </p>
 *
 * @author dx
 * @since 2019-09-24
 */
public interface PatienttypeService extends IService<PatienttypeBean> {

    //查询患者类型
    public List<PatienttypeBean> queryPatientType(PatienttypeBean patienttypeBean);

    //添加患者类型
    public void addPatientType(PatienttypeBean patienttypeBean) throws MessageException;

    //修改患者类型
    public void updatePatientType(PatienttypeBean patienttypeBean) throws MessageException;
}
