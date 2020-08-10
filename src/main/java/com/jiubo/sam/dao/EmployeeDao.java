package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    List<EmployeeBean> getEmpByCondition(Page page, @Param("employeeBean") EmployeeBean employeeBean);
}
