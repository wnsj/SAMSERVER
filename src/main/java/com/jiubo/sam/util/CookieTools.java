package com.jiubo.sam.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class CookieTools {


    /**
     * 清除指定cookie名 必须回写到客户端 否则不生效
     *
     * @param cokiename
     * @param response
     */
    public static void ClearCookie(String cokiename, HttpServletResponse response) {
        Cookie cookie;
        cookie = new Cookie(cokiename, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /**
     * 设置cookie
     *
     * @param response
     * @param name     cookie名字
     * @param value    cookie值
     * @param maxAge   cookie生命周期  以秒为单位
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        if (maxAge > 0) {
            cookie.setMaxAge(maxAge);
        }
        response.addCookie(cookie);
    }

    /**
     * 获取所有cookie
     *
     * @param request
     */
    public void SearchAllCookies(HttpServletRequest request) {
        Cookie[] s = request.getCookies();
        for (Cookie cookie : s) {
            System.out.println("key:" + cookie.getName() + ",value:" + cookie.getValue());
        }
    }

    /**
     * 根据名字获取cookie
     *
     * @param request
     * @param name    cookie名字
     * @return
     */
    public static Cookie getCookieName(HttpServletRequest request, String name) {
        Map<String, Cookie> cookieMap = ReadCookieMap(request);
        if (cookieMap.containsKey(name)) {
            Cookie cookie = (Cookie) cookieMap.get(name);
            return cookie;
        } else {
            return null;
        }
    }

    /**
     * 获取cookie指定name的value
     *
     * @param request
     * @param key
     * @return
     */
    public static String getCookieValue(HttpServletRequest request, String key) {
        Cookie[] s = request.getCookies();
        if (null != s) {
            for (Cookie cookie : s) {
                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
                //System.out.println("key:"+cookie.getName()+",value:"+cookie.getValue());
            }
        }
        return "";
    }


    /**
     * 将cookie封装到Map里面
     *
     * @param request
     * @return
     */
    public static Map<String, Cookie> ReadCookieMap(HttpServletRequest request) {
        Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }
}
