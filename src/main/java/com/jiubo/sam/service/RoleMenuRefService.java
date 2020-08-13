package com.jiubo.sam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiubo.sam.bean.RoleMenuRefBean;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author dx
 * @since 2020-05-20
 */
public interface RoleMenuRefService extends IService<RoleMenuRefBean> {

    public void addRMRef(RoleMenuRefBean roleMenuRefBean);

    public void patchRMRef(RoleMenuRefBean roleMenuRefBean);
}

