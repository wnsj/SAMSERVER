package com.jiubo.sam.dao;

import com.jiubo.sam.bean.DepartmentBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  科室Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
public interface DepartmentDao extends BaseMapper<DepartmentBean> {

    //查询科室
    public List<DepartmentBean> queryDepartment(DepartmentBean departmentBean);

    //添加科室
    //public void addDepartment(DepartmentBean departmentBean);

    //修改和逻辑删除科室
    //public int updateDepartment(DepartmentBean departmentBean);
}
