package com.jiubo.sam.dao;

import com.jiubo.sam.bean.AdmissionRecordsBean;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 入院记录表 Mapper 接口
 * </p>
 *
 * @author dx
 * @since 2020-10-17
 */
public interface AdmissionRecordsDao extends BaseMapper<AdmissionRecordsBean> {

    //查询出入院信息
    List<AdmissionRecordsBean> queryAdmissionRecord(AdmissionRecordsBean admissionRecordsBean);

    //插入数据
    int insertAdmissionRecord(AdmissionRecordsBean admissionRecordsBean);

    //修改数据
    int updateAdmissionRecord(AdmissionRecordsBean admissionRecordsBean);
}
