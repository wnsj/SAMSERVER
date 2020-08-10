package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.EmployeeBean;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author syl
 * @since 2020-08-06
 */
public interface EmployeeService extends IService<EmployeeBean> {

    Page<EmployeeBean> getEmpByCondition(EmployeeBean employeeBean);

    void addEmp(EmployeeBean employeeBean);

    void patchEmp(EmployeeBean employeeBean);
}
