package com.jiubo.sam.service;

import com.jiubo.sam.bean.PatinetMarginBean;
import com.jiubo.sam.dto.RemarkDto;
import com.jiubo.sam.exception.MessageException;

public interface PatinetMarginService {
    void addPatinetMargin(PatinetMarginBean patinetMarginBean) throws Exception;

    int updateMarginRemark(RemarkDto remarkDto) throws MessageException;

    int updateMeRemark(RemarkDto remarkDto) throws MessageException;
}
