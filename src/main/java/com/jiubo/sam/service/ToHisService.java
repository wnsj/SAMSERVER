package com.jiubo.sam.service;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.dto.CACondition;
import com.jiubo.sam.dto.CheckAccount;
import com.jiubo.sam.exception.MessageException;

import java.util.List;

public interface ToHisService {

    int addHisEmp(JSONObject jsonObject) throws MessageException;

    int refundOrAddHP(JSONObject jsonObject) throws Exception;

    List<CheckAccount> getCATable(CACondition condition);
}
