package com.jiubo.sam.service;

import com.jiubo.sam.bean.AdmissionRecordsBean;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 入院记录表 服务类
 * </p>
 *
 * @author dx
 * @since 2020-10-17
 */
public interface AdmissionRecordsService extends IService<AdmissionRecordsBean> {

    //添加出入院记录
    public void addAdmissionRecord(AdmissionRecordsBean admissionRecordsBean) throws Exception;


    //查询出入院记录
    public List<AdmissionRecordsBean> queryAdmissionRecord(AdmissionRecordsBean admissionRecordsBean) throws Exception;

}
