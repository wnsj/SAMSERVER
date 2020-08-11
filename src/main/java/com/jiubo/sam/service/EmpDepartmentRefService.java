package com.jiubo.sam.service;

import com.jiubo.sam.bean.EmpDepartmentRefBean;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author syl
 * @since 2020-08-10
 */
public interface EmpDepartmentRefService extends IService<EmpDepartmentRefBean> {
    List<Long> getEdRefByEmpId(EmpDepartmentRefBean empDepartmentRefBean);

}
