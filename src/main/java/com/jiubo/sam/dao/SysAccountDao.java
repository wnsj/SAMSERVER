package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.SysAccountBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author mwl
 * @since 2020-02-10
 */
public interface SysAccountDao extends BaseMapper<SysAccountBean> {
    public List<SysAccountBean> queryAccountList(@Param("accountBean") SysAccountBean accountBean);
    public List<SysAccountBean> queryAccountList(Page page, @Param("accountBean") SysAccountBean accountBean);

    public int addAccount(SysAccountBean accountBean);

    public void patchAccount(SysAccountBean accountBean);

    public void deleteAccById(@Param("saId") Integer saId);
}

