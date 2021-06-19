package com.jiubo.sam.service;

import cn.hutool.json.JSONObject;
import com.jiubo.sam.dto.CACondition;
import com.jiubo.sam.dto.CheckAccount;

import java.util.List;

public interface ToHisService {

    int addHisEmp(JSONObject jsonObject);

    int refundOrAddHP(JSONObject jsonObject) throws Exception;

    List<CheckAccount> getCATable(CACondition condition);
}
