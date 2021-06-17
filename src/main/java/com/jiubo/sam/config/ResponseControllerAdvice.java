package com.jiubo.sam.config;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiubo.sam.common.Constant;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Type;

/**
 * 全局处理响应数据
 */
@RestControllerAdvice(basePackages = {"com.jiubo.sam.action"}) // 注意哦，这里要加上需要扫描的包
public class ResponseControllerAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> clazz) {
        // 如果接口返回的类型本身就是JSONObject那就没有必要进行额外的操作，返回false
        return !methodParameter.getParameterType().equals(JSONObject.class);
    }

    @Override
    public Object beforeBodyWrite(Object data, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> clazz, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        Type type = methodParameter.getGenericParameterType();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constant.Result.RETCODE, Constant.Result.SUCCESS);
        jsonObject.put(Constant.Result.RETMSG, Constant.Result.SUCCESS_MSG);
        jsonObject.put(Constant.Result.RETDATA, data);
        if (!type.equals(String.class)) {
            // 将原本的数据包装在JSONObject里
            return jsonObject;
        } else {
            // String类型不能直接包装，所以要进行些特别的处理
            ObjectMapper objectMapper = new ObjectMapper();
            // 将数据包装在JSONObject里后，再转换为json字符串响应给前端
            try {
                return objectMapper.writeValueAsString(jsonObject);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("json转换错误!");
            }
        }
    }
}
