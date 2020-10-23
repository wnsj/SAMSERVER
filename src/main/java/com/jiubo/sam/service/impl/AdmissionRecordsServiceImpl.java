package com.jiubo.sam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiubo.sam.bean.AdmissionRecordsBean;
import com.jiubo.sam.dao.AdmissionRecordsDao;
import com.jiubo.sam.exception.MessageException;
import com.jiubo.sam.service.AdmissionRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 入院记录表 服务实现类
 * </p>
 *
 * @author dx
 * @since 2020-10-17
 */
@Service
public class AdmissionRecordsServiceImpl extends ServiceImpl<AdmissionRecordsDao, AdmissionRecordsBean> implements AdmissionRecordsService {

    @Autowired
    private AdmissionRecordsDao admissionRecordsDao;

    @Override
    public void addAdmissionRecord(AdmissionRecordsBean admissionRecordsBean) throws Exception {
        List<AdmissionRecordsBean> admissionRecordsBeanList = admissionRecordsDao.queryAdmissionRecord(new AdmissionRecordsBean().setHospNum(admissionRecordsBean.getHospNum()));
        if (admissionRecordsBeanList.size()>0){
            if (admissionRecordsBean.getIsHos()==1 && admissionRecordsBeanList.get(0).getIsHos()==0){
                if (admissionRecordsDao.insertAdmissionRecord(admissionRecordsBean) <= 0) throw new MessageException("操作失败!");
            }else {
                admissionRecordsDao.updateAdmissionRecord(admissionRecordsBeanList.get(0)
                        .setIsHos(admissionRecordsBean.getIsHos())
                        .setArInDate(admissionRecordsBean.getArInDate())
                        .setArOutDate(admissionRecordsBean.getArOutDate()));
            }
        }else {
            if (admissionRecordsDao.insertAdmissionRecord(admissionRecordsBean) <= 0) throw new MessageException("操作失败!");
        }
    }

    @Override
    public List<AdmissionRecordsBean> queryAdmissionRecord(AdmissionRecordsBean admissionRecordsBean) throws Exception {

        return admissionRecordsDao.queryAdmissionRecord(admissionRecordsBean);
    }
}
