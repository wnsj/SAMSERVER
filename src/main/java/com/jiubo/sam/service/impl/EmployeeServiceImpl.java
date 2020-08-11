package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.EmpDepartmentRefBean;
import com.jiubo.sam.bean.EmployeeBean;
import com.jiubo.sam.dao.EmpDepartmentRefDao;
import com.jiubo.sam.dao.EmployeeDao;
import com.jiubo.sam.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.util.CollectionsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author syl
 * @since 2020-08-06
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, EmployeeBean> implements EmployeeService {

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private EmpDepartmentRefDao empDepartmentRefDao;

    @Override
    public Page<EmployeeBean> getEmpByCondition(EmployeeBean employeeBean) {
        Page<EmployeeBean> page = new Page<>();
        page.setCurrent(employeeBean.getCurrent());
        page.setSize(employeeBean.getPageSize());
        List<EmployeeBean> employeeBeanList = employeeDao.getEmpByCondition(employeeBean);
        List<EmployeeBean> list = null;
        if (!CollectionsUtils.isEmpty(employeeBeanList)) {
            list = employeeBeanList.stream().sorted(Comparator.comparing(EmployeeBean::getId)).skip((page.getCurrent() -1) * page.getSize()).limit(page.getSize()).collect(Collectors.toList());
        }
        if (!CollectionsUtils.isEmpty(list)) {
            for (EmployeeBean bean : list) {
                List<EmpDepartmentRefBean> deptIdList = empDepartmentRefDao.getEdRefByEmpId(bean.getId());
                if (!CollectionsUtils.isEmpty(deptIdList)) {
                    List<Long> list1 = deptIdList.stream().map(EmpDepartmentRefBean::getDeptId).collect(Collectors.toList());
                    bean.setDeptIdList(list1);
                }
            }
        }
        page.setTotal(employeeBeanList.size());
        return page.setRecords(list);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void addEmp(EmployeeBean employeeBean) {
        employeeBean.setCreateDate(new Date());
        employeeDao.addEmp(employeeBean);
        Long id = employeeBean.getId();
        if (id != 0 && !CollectionsUtils.isEmpty(employeeBean.getDeptIdList())) {
            addEdRef(employeeBean, id);
        }
    }

    private void addEdRef(EmployeeBean employeeBean, Long id) {
        List<EmpDepartmentRefBean> refBeans = new ArrayList<>();
        for (Long deptId : employeeBean.getDeptIdList()) {
            EmpDepartmentRefBean bean = new EmpDepartmentRefBean();
            bean.setEmpId(id);
            bean.setDeptId(deptId);
            bean.setCreateDate(new Date());
            refBeans.add(bean);
        }
        empDepartmentRefDao.addEdRef(refBeans);
    }

    @Transactional(readOnly = false, rollbackFor = Exception.class)
    @Override
    public void patchEmp(EmployeeBean employeeBean) {
        employeeDao.updateById(employeeBean);
        if (!CollectionsUtils.isEmpty(employeeBean.getDeptIdList())) {
            empDepartmentRefDao.deleteByEmpId(employeeBean.getId());
            addEdRef(employeeBean, employeeBean.getId());
        }
    }
}
