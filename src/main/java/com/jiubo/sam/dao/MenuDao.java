package com.jiubo.sam.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiubo.sam.bean.MenuBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author syl
 * @since 2020-05-21
 */
public interface MenuDao extends BaseMapper<MenuBean> {

    public List<MenuBean> getMenuByRoleIdList(@Param("menuBean") MenuBean menuBean);
    public List<MenuBean> getMenuByRoleIdList(Page page, @Param("menuBean") MenuBean menuBean);


    List<MenuBean> selectMenuByLevelOrParentId(@Param("level") int level, @Param("parentId") int parentId);

    public int addMenu(@Param("menuBean") MenuBean menuBean);

    public void patchMenuById(@Param("menuBean") MenuBean menuBean);
}
