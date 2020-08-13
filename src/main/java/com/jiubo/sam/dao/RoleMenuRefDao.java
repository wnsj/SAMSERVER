package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.jiubo.sam.bean.RoleMenuRefBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2020-05-20
 */
public interface RoleMenuRefDao extends BaseMapper<RoleMenuRefBean> {
    public List<RoleMenuRefBean> getRMRByCondition(@Param("roleMenuRefBean") RoleMenuRefBean roleMenuRefBean);

    public void addRMR(List<RoleMenuRefBean> roleMenuRefBeans);

    public void deleteRMRByRoleId(@Param("roleId") Integer roleId);
}

