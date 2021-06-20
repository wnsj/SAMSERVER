package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.DepartmentBean;
import com.jiubo.sam.bean.EmpDepartmentRefBean;
import com.jiubo.sam.bean.EmployeeBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author syl
 * @since 2020-08-06
 */
public interface EmployeeDao extends BaseMapper<EmployeeBean> {
    List<EmployeeBean> getEmpByCondition(@Param("employeeBean") EmployeeBean employeeBean);
    int addEmp(EmployeeBean employeeBean);

    int updateEmpBatch(List<EmployeeBean> list);

    List<EmployeeBean> getAllEmp();
//    int deleteAllRef();

    void insertAll(List<EmpDepartmentRefBean> list);

    void delectAll(List<Integer>list);
}
