package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.RoleBean;
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
public interface RoleDao extends BaseMapper<RoleBean> {
    public List<RoleBean> getRoleByIdList(@Param("roleBean") RoleBean roleBean);
    public List<RoleBean> getRoleByIdList(Page page, @Param("roleBean") RoleBean roleBean);

    public int addRole(@Param("roleBean") RoleBean roleBean);

    public void patchRoleById(@Param("roleBean") RoleBean roleBean);
}

