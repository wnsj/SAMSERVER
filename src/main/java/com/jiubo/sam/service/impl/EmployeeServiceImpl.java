package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.EmployeeBean;
import com.jiubo.sam.dao.EmployeeDao;
import com.jiubo.sam.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author syl
 * @since 2020-08-06
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, EmployeeBean> implements EmployeeService {

    @Autowired
    private EmployeeDao employeeDao;

    @Override
    public Page<EmployeeBean> getEmpByCondition(EmployeeBean employeeBean) {
        Page<EmployeeBean> page = new Page<>();
        page.setCurrent(employeeBean.getCurrent());
        page.setSize(employeeBean.getPageSize());
        return page.setRecords(employeeDao.getEmpByCondition(page,employeeBean));
    }

    @Override
    public void addEmp(EmployeeBean employeeBean) {
        employeeBean.setCreateDate(new Date());
        employeeDao.insert(employeeBean);
    }

    @Override
    public void patchEmp(EmployeeBean employeeBean) {
        employeeDao.updateById(employeeBean);
    }
}
