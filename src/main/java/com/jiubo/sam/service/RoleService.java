package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.bean.RoleBean;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dx
 * @since 2020-05-20
 */
public interface RoleService extends IService<RoleBean> {
    public List<RoleBean> getAllRole();

    public Page<RoleBean> getRoleByCondition(RoleBean roleBean);

    public void addRole(RoleBean roleBean);

    public void patchRoleById(RoleBean roleBean);
}

