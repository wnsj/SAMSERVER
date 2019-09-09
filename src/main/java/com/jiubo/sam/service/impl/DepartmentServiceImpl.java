package com.jiubo.sam.service.impl;

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
 *  科室服务实现类
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
       if(departmentDao.insert(departmentBean) <= 0) throw new MessageException("操作失败!");
    }

    @Override
    public void updateDepartment(DepartmentBean departmentBean) throws MessageException {
        if(departmentDao.updateById(departmentBean) <= 0)throw new MessageException("操作失败!");
    }
}
