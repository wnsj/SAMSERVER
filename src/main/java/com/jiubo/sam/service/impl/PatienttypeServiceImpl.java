package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jiubo.sam.bean.PatienttypeBean;
import com.jiubo.sam.dao.PatienttypeDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.PatienttypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 患者类型服务实现类
 * </p>
 *
 * @author dx
 * @since 2019-09-24
 */
@Service
public class PatienttypeServiceImpl extends ServiceImpl<PatienttypeDao, PatienttypeBean> implements PatienttypeService {

    @Autowired
    private PatienttypeDao patienttypeDao;

    @Override
    public List<PatienttypeBean> queryPatientType(PatienttypeBean patienttypeBean) {
        return patienttypeDao.queryPatientType(patienttypeBean);
    }

    @Override
    public void addPatientType(PatienttypeBean patienttypeBean) throws MessageException {
        if (StringUtils.isBlank(patienttypeBean.getPatitypename())) throw new MessageException("患者类型名不能为空!");
        if (queryPatientTypeByName(patienttypeBean).size() > 0) throw new MessageException("患者类型名已存在!");
        if (patienttypeDao.insert(patienttypeBean) <= 0) throw new MessageException("操作失败!");
    }

    @Override
    public void updatePatientType(PatienttypeBean patienttypeBean) throws MessageException {
        if (StringUtils.isBlank(patienttypeBean.getPatitypename())) throw new MessageException("患者类型名不能为空!");
        QueryWrapper<PatienttypeBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.ne("PATITYPEID", patienttypeBean.getPatitypeid());
        queryWrapper.eq(true, "PATITYPENAME", patienttypeBean.getPatitypename());
        if (patienttypeDao.selectList(queryWrapper).size() > 0) throw new MessageException("患者类型名已存在!");
        if (patienttypeDao.updateById(patienttypeBean) <= 0) throw new MessageException("操作失败!");
    }

    @Override
    public List<PatienttypeBean> queryPatientTypeByName(PatienttypeBean patienttypeBean) {
        QueryWrapper<PatienttypeBean> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("*");
        queryWrapper.eq(true, "PATITYPENAME", patienttypeBean.getPatitypename());
        return patienttypeDao.selectList(queryWrapper);
    }
}
