package com.jiubo.sam.service.impl;

import com.jiubo.sam.bean.PatientBean;
import com.jiubo.sam.dao.PatientDao;
import com.jiubo.sam.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 患者基础信息 服务实现类
 * </p>
 *
 * @author dx
 * @since 2019-09-07
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientDao, PatientBean> implements PatientService {

}
