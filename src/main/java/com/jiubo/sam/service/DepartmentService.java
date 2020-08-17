package com.jiubo.sam.service;

import com.jiubo.sam.bean.DepartmentBean;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.exception.MessageException;

import java.util.List;

/**
 * <p>
 * 科室服务类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface DepartmentService extends IService<DepartmentBean> {

    //查询科室
    public List<DepartmentBean> queryDepartment(DepartmentBean departmentBean) throws MessageException;

    //添加科室
    public void addDepartment(DepartmentBean departmentBean) throws MessageException;

    //修改科室
    public void updateDepartment(DepartmentBean departmentBean) throws MessageException;


    //修改科室
    public void updateDepartmentById(List<DepartmentBean> departmentBeans) throws Exception;

    //根据科室名查询科室信息
    public List<DepartmentBean> queryDeptByName(DepartmentBean departmentBean) throws MessageException;

    //根据科室ID查询科室欠费情况
    public List<DepartmentBean> queryArrearsByDept(DepartmentBean departmentBean) throws Exception;
}
