package com.jiubo.sam.service;

import cn.hutool.json.JSONObject;

public interface ToHisService {

    int addHisEmp(JSONObject jsonObject);

    void refundHP(JSONObject jsonObject);
}
