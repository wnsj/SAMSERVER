package com.jiubo.sam.service;

import cn.hutool.json.JSONObject;

public interface ToHisService {

    int addHisEmp(JSONObject jsonObject);

    int refundOrAddHP(JSONObject jsonObject) throws Exception;
}
