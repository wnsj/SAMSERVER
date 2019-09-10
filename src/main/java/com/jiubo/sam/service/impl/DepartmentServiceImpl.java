package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.jiubo.sam.bean.DepartmentBean;
import com.jiubo.sam.dao.DepartmentDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.DepartmentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 科室服务实现类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentDao, DepartmentBean> implements DepartmentService {

    @Autowired
    private DepartmentDao departmentDao;

    @Override
    public List<DepartmentBean> queryDepartment(DepartmentBean departmentBean) throws MessageException {
        return departmentDao.queryDepartment(departmentBean);
    }

    @Override
    public void addDepartment(DepartmentBean departmentBean) throws MessageException {
        QueryWrapper<DepartmentBean> queryWrapper = new QueryWrapper<DepartmentBean>();
        queryWrapper.eq(true, "NAME", departmentBean.getName());
        List<DepartmentBean> departmentBeans = departmentDao.selectList(queryWrapper);
        if(departmentBeans.size() > 0)throw new MessageException("科室名不能重复!");
        if (departmentDao.insert(departmentBean) <= 0) throw new MessageException("操作失败!");
    }

    @Override
    public void updateDepartment(DepartmentBean departmentBean) throws MessageException {
        QueryWrapper<DepartmentBean> queryWrapper = new QueryWrapper<DepartmentBean>();
        queryWrapper.eq(true, "NAME", departmentBean.getName());
        queryWrapper.ne("DEPT_ID", departmentBean.getDeptId());
        List<DepartmentBean> departmentBeans = departmentDao.selectList(queryWrapper);
        if(departmentBeans.size() > 0)throw new MessageException("科室名不能重复!");
        if (departmentDao.updateById(departmentBean) <= 0) throw new MessageException("操作失败!");
    }
}
