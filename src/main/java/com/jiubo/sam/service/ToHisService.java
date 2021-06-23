package com.jiubo.sam.service;

import com.alibaba.fastjson.JSONObject;
import com.jiubo.sam.dto.CACondition;
import com.jiubo.sam.dto.CheckAccount;
import com.jiubo.sam.exception.MessageException;

import java.util.List;

public interface ToHisService {

    int addHisEmp(String param) throws MessageException;

    JSONObject refundOrAddHP(String param) throws Exception;

    List<CheckAccount> getCATable(CACondition condition);
}
