package com.jiubo.sam.dao;

import com.jiubo.sam.bean.EmpDepartmentRefBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author syl
 * @since 2020-08-10
 */
public interface EmpDepartmentRefDao extends BaseMapper<EmpDepartmentRefBean> {
    void deleteByEmpId(@Param("empId") Long empId);
    void addEdRef(List<EmpDepartmentRefBean> empDepartmentRefBeanList);
    List<EmpDepartmentRefBean> getEdRefByEmpId(@Param("empId") Long empId);
}
