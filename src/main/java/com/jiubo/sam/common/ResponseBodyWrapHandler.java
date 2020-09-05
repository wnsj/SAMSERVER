package com.jiubo.sam.common;

import com.alibaba.fastjson.JSONObject;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 *  统一API返回JSON数据格式Handler
 */
public class ResponseBodyWrapHandler implements HandlerMethodReturnValueHandler {

    private final HandlerMethodReturnValueHandler delegate;

    public ResponseBodyWrapHandler(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return delegate.supportsReturnType(methodParameter);
    }

    @Override
    public void handleReturnValue(Object body, MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest) throws Exception {
        if (body instanceof JSONObject) {

        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("retCode", "0000");
            jsonObject.put("retData", body);
            jsonObject.put("retMsg", "成功!");
            body = jsonObject;
        }
        delegate.handleReturnValue(body, methodParameter, modelAndViewContainer, nativeWebRequest);
    }
}
