package com.jiubo.sam.util;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.jiubo.sam.common.Constant;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

public class MapUtil {

    public static final int ALLOW_NULL = 1;
    public static final int NOT_NULL = 0;
    public final static String SPEC_KEY = "class";
    public final static String REQUEST_KEY = "request";
    public final static String RESPONSE_KEY = "response";
    public final static String PREFIX_UNDERLINE = "_";
    public final static String PREFIX_NULLSTR = "";


    public static ValueFilter JSON_NULL_VALUE_FILTER = new ValueFilter() {
        @Override
        public Object process(Object obj, String s, Object v) {
            if (v == null)
                return PREFIX_NULLSTR;
            return v;
        }
    };

    public static ValueFilter JSON_NULL_VALUE_YYYY_MM_DD_FILTER = new ValueFilter() {
        @Override
        public Object process(Object obj, String s, Object v) {
            if (v == null)
                return PREFIX_NULLSTR;
            if (v instanceof Date)
                return TimeUtil.getDateYYYY$MM$DD((Date) v);
            return v;
        }
    };


    static {
        ConvertUtils.register(new Converter() {
            public Object convert(Class type, Object value) {
                //判断是不是String类型的数据，不是则抛出异常
                if (!(value instanceof String)) return value;
                try {
                    return TimeUtil.parseAnyDate((String) value);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }, Date.class);
    }

    /**
     * 求Map<K,V>中Key(键)的最大值
     *
     * @param map
     * @return
     */
    public static Object getMaxKey(Map map) {
        if (map == null) return null;
        Set<Object> set = map.keySet();
        Object[] obj = set.toArray();
        Arrays.sort(obj);
        return obj[obj.length - 1];
    }

    /**
     * 求Map<K,V>中Value(值)的最大值
     *
     * @param map
     * @return
     */
    public static Object getMaxValue(Map map) {
        if (map == null) return null;
        Collection<Object> c = map.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        return obj[obj.length - 1];
    }

    public static Object getKeyByValue(Map map, Object value) {
        Object keys = PREFIX_NULLSTR;
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            Object obj = entry.getValue();
            if (obj != null && obj.equals(value)) {
                keys = entry.getKey();
            }

        }
        return keys;
    }

    /**
     * 求Map<K,V>中Key(键)的最小值
     *
     * @param map
     * @return
     */
    public static Object getMinKey(Map map) {
        if (map == null) return null;
        Set<Object> set = map.keySet();
        Object[] obj = set.toArray();
        Arrays.sort(obj);
        return obj[0];
    }

    /**
     * 求Map<K,V>中Value(值)的最小值
     *
     * @param map
     * @return
     */
    public static Object getMinValue(Map map) {
        if (map == null) return null;
        Collection<Object> c = map.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        return obj[0];
    }

    public static String getString(Map map, String key, int is_null) throws Exception {
        Object obj = map.get(key);
        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return null;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        if (obj instanceof Date) {
            return TimeUtil.getDateYYYY_MM_DD_HH_MM_SS((Date) obj);
        }
        return obj.toString();
    }

    public static String getStringIgnoreCase(Map map, String key, int is_null) throws Exception {
        if (map == null) throw new Exception("map is null !");
        map = transKeyLowerCase(map, Boolean.FALSE);
        Object obj = map.get(key != null ? key.toLowerCase() : key);
        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return null;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }
        if (obj instanceof Date) {
            return TimeUtil.getDateYYYY_MM_DD_HH_MM_SS((Date) obj);
        }
        return obj.toString();
    }


    public static int getInt(Map map, String key, int is_null) throws Exception {
        Object obj = map.get(key);

        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return -1;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        return Integer.parseInt(obj.toString());
    }

    public static int getIntIgnoreCase(Map map, String key, int is_null) throws Exception {
        map = transKeyLowerCase(map, Boolean.FALSE);
        if (map == null) throw new Exception("map is null !");
        Object obj = map.get(key != null ? key.toLowerCase() : key);
        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return -1;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        return Integer.parseInt(obj.toString());
    }

    public static long getLong(Map map, String key, int is_null) throws Exception {
        Object obj = map.get(key);

        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return -1;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        return Long.parseLong(obj.toString());
    }

    public static long getLongIgnoreCase(Map map, String key, int is_null) throws Exception {
        map = transKeyLowerCase(map, Boolean.FALSE);
        if (map == null) throw new Exception("map is null !");
        Object obj = map.get(key != null ? key.toLowerCase() : key);
        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return -1;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        return Long.parseLong(obj.toString());
    }

    public static double getDouble(Map map, String key, int is_null) throws Exception {
        Object obj = map.get(key);

        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return -1.00;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        return Double.parseDouble(obj.toString());
    }

    public static double getDoubleIgnoreCase(Map map, String key, int is_null) throws Exception {
        map = transKeyLowerCase(map, Boolean.FALSE);
        if (map == null) throw new Exception("map is null !");
        Object obj = map.get(key != null ? key.toLowerCase() : key);
        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return -1.00;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        return Double.parseDouble(obj.toString());
    }

    public static List getList(Map map, String key, int is_null) throws Exception {
        Object obj = map.get(key);

        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return new ArrayList();
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        if (!(obj instanceof List)) {
            throw new Exception(key + "对应的不是List");
        }

        return (List) obj;
    }


    public static List getRealList(Map map, String key, int is_null) throws Exception {
        Object obj = map.get(key);

        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return new ArrayList();
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }
        if (obj instanceof Map) {
            List list = new ArrayList();
            list.add(obj);
            obj = list;
        }
        if (!(obj instanceof List)) {
            throw new Exception(key + "对应的不是List");
        }

        return (List) obj;
    }


    public static String getString(Map map, int key, int is_null) throws Exception {
        Object obj = map.get(key);

        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return null;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        return obj.toString();
    }

    public static int getInt(Map map, int key, int is_null) throws Exception {
        Object obj = map.get(key);

        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return -1;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        return Integer.parseInt(obj.toString());
    }

    public static long getLong(Map map, int key, int is_null) throws Exception {
        Object obj = map.get(key);

        if (obj == null || obj.toString().length() <= 0) {
            if (is_null == 1) {
                return -1;
            } else {//如果没指定是否允许为空，默认不允许为空
                throw new Exception(key + "不能为空");
            }
        }

        return Long.parseLong(obj.toString());
    }

    public static Map<String, String> getParameterMap(ServletRequest request) throws Exception {
        // 参数Map
        Map properties = request.getParameterMap();
        // 返回值Map
        Map<String, String> returnMap = new HashMap<String, String>();
        Iterator entries = properties.entrySet().iterator();
        Entry entry;
        String name = PREFIX_NULLSTR;
        String value = PREFIX_NULLSTR;
        while (entries.hasNext()) {
            entry = (Entry) entries.next();
            name = (String) entry.getKey();
            Object valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else if (valueObj instanceof String[]) {
                String[] values = (String[]) valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
                value = URLDecoder.decode(value, Constant.Charset.UTF8);
            } else {
                value = valueObj.toString();
                value = URLDecoder.decode(value, Constant.Charset.UTF8);
            }

            returnMap.put(name, value);
        }
        return returnMap;
    }

    //获取request字段
    public static Map<String, Object> getParameterContainsRequestMap(ServletRequest request, ServletResponse response) throws Exception {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.putAll(getParameterMap(request));
        returnMap.put(REQUEST_KEY, request);
        returnMap.put(RESPONSE_KEY, response);
        return returnMap;
    }

    //获取request字段
    public static Map<String, Object> getRequestParameters(ServletRequest request) throws Exception {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.putAll(getParameterMap(request));
        return returnMap;
    }

    /*
     * Des:将Map的Key转小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static Map transKeyLowerCase(Map orgMap, Boolean isDealUnderLine) {
        if (orgMap == null || orgMap.isEmpty()) {
            return orgMap;
        }
        Map retMap = new LinkedHashMap();

        Set<String> keySet = orgMap.keySet();
        for (String key : keySet) {
            if (isDealUnderLine) {
                String tempKey = key;
                retMap.put(key != null ? tempKey.substring(0, 1).toLowerCase().concat(key.toLowerCase().substring(1).replaceAll(PREFIX_UNDERLINE, PREFIX_NULLSTR)) : key, orgMap.get(key));
            } else {
                retMap.put(key != null ? key.toLowerCase() : key, orgMap.get(key));
            }
        }
        return retMap;
    }

    /*
     * Des:将Json的Key转小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static JSONObject transKeyLowerCase(JSONObject orgJson, Boolean isDealUnderLine) {
        if (orgJson == null || orgJson.isEmpty()) {
            return orgJson;
        }
        JSONObject retJson = new JSONObject();

        Set<String> keySet = orgJson.keySet();
        for (String key : keySet) {
            if (isDealUnderLine) {
                String tempKey = key;
                retJson.put(key != null ? tempKey.substring(0, 1).toLowerCase().concat(key.toLowerCase().substring(1).replaceAll(PREFIX_UNDERLINE, PREFIX_NULLSTR)) : key, orgJson.get(key));
            } else {
                retJson.put(key != null ? key.toLowerCase() : key, orgJson.get(key));
            }

        }
        return retJson;
    }

    /*
     * Des:将Map对象转JavaBean,不区分键值大小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static <T> T transMapToObjectIgnoreCase(Map map, Class<T> clz) throws Exception {
        return transMapToObjectIgnoreCase(map, clz, Boolean.TRUE);
    }

    /*
     * Des:将Map对象转JavaBean,不区分键值大小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static <T> T transMapToObjectIgnoreCase(Map map, Class<T> clz, Boolean isDealUnderLine) throws Exception {
        if (map == null) return null;
        //对map做特殊处理
        map = transKeyLowerCase(map, isDealUnderLine);
        T obj = clz.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            if (!map.containsKey(property.getName().toLowerCase())) continue;
            org.apache.commons.beanutils.BeanUtils.setProperty(obj, property.getName(), map.get(property.getName() != null ? property.getName().toLowerCase() : property.getName()));
        }
        return obj;
    }


    /*
     * Des:将Json对象转JavaBean,不区分键值大小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static <T> T transJsonToObjectIgnoreCase(JSONObject json, Class<T> clz) throws Exception {
        if (json == null) return null;
        //对map做特殊处理
        json = transKeyLowerCase(json, Boolean.TRUE);
        T obj = clz.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            if (!json.containsKey(property.getName().toLowerCase())) continue;
            org.apache.commons.beanutils.BeanUtils.setProperty(obj, property.getName(), json.get(property.getName() != null ? property.getName().toLowerCase() : property.getName()));
        }
        return obj;
    }

    /*
     * Des:将Json字符串转JavaBean,不区分键值大小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static <T> T transJsonStrToObjectIgnoreCase(String jsonStr, Class<T> clz) throws Exception {
        return transJsonStrToObjectIgnoreCase(jsonStr, clz, Boolean.TRUE);
    }

    /*
     * Des:将Json字符串转JavaBean,不区分键值大小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static <T> T transJsonStrToObjectIgnoreCase(String jsonStr, Class<T> clz, Boolean isDealUnderLine) throws Exception {
        if (StringUtils.isBlank(jsonStr)) return null;
        JSONObject json = JSON.parseObject(jsonStr);
        //对map做特殊处理
        json = transKeyLowerCase(json, Boolean.TRUE);
        if (json == null) throw new Exception("json is null!");
        T obj = clz.newInstance();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor property : propertyDescriptors) {
            if (!json.containsKey(property.getName().toLowerCase())) continue;
            org.apache.commons.beanutils.BeanUtils.setProperty(obj, property.getName(), json.get(property.getName() != null ? property.getName().toLowerCase() : property.getName()));
        }
        return obj;
    }

    /*
     * Des:将Json对象转JavaBean,不区分键值大小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static <T> List<T> transJsonToObjectIgnoreCase(JSONArray jsonArray, Class<T> clz) throws Exception {
        List<T> retList = new ArrayList<>();
        if (jsonArray == null || jsonArray.isEmpty()) return retList;

        for (int i = 0; i < jsonArray.size(); i++) {
            retList.add(transJsonToObjectIgnoreCase(jsonArray.getJSONObject(i), clz));
        }
        return retList;
    }


    /*
     * Des:将Map对象转JavaBean,不区分键值大小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static <T> List<T> transMapToObjectIgnoreCase(List<Map<String, Object>> obj, Class<T> clz) throws Exception {
        return transMapToObjectIgnoreCase(obj, clz, Boolean.TRUE);
    }

    /*
     * Des:将Map对象转JavaBean,不区分键值大小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static <T> List<T> transMapToObjectIgnoreCase(List<Map<String, Object>> obj, Class<T> clz, Boolean isDealUnderLine) throws Exception {
        List<T> retList = new ArrayList<>();
        if (obj == null || obj.isEmpty()) return retList;

        for (int i = 0; i < obj.size(); i++) {
            retList.add(transMapToObjectIgnoreCase(obj.get(i), clz, isDealUnderLine));
        }
        return retList;
    }


    /*
     * Des:将JavaBean对象转Map
     * Author:gaodd
     * Date:2018-07-03
     */
    public static Map<String, Object> transObjectToMap(Object object) throws Exception {
        Map<String, Object> returnMap = PropertyUtils.describe(object);
        returnMap.remove(SPEC_KEY);
        return returnMap;
    }


    /*
     * Des:将JavaBean对象转Map
     * Author:gaodd
     * Date:2018-06-27
     */
    public static List<Map<String, Object>> transObjectToMap(List objList) throws Exception {
        List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
        if (objList == null || objList.isEmpty()) return retList;

        for (Object o : objList)
            retList.add(transObjectToMap(o));

        return retList;
    }


    /*
     * Des:将request对象转JavaBean,不区分键值大小写
     * Author:gaodd
     * Date:2018-06-27
     */
    public static <T> T transRequestToObjectIgnoreCase(ServletRequest request, Class<T> clz) throws Exception {
        Map<String, String> params = MapUtil.getParameterMap(request);
        return transMapToObjectIgnoreCase(params, clz);
    }


}

/*
	public static JSONObject getParameterFromInputStream(ServletRequest request,ServletResponse response) throws Exception{
		InputStream is = request.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer sbf = new StringBuffer();
		char[] ch = new char[128];
		int len = 0;
		while((len = br.read(ch, 0, ch.length)) != -1) {
			sbf.append(ch, 0, len);
		}
		String data = new String(sbf.toString().getBytes("UTF-8"));
		if(StringUtils.isNotBlank(data)){
			return JSONObject.parseObject(data);
		}
		return null;
	}
	
public static com.alibaba.fastjson.JSONObject getJSONObjFromInputStream(HttpServletRequest request) throws Exception{
		String s = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
		if(StringUtils.isNotBlank(s)) {
			return com.alibaba.fastjson.JSONObject.parseObject(s);
		}
		return null;
	}
	
	public static Map<String,Object> getMapFromInputStream(HttpServletRequest request)throws Exception{
		com.alibaba.fastjson.JSONObject json = getJSONObjFromInputStream(request);
		if(json == null)return null;
		else return com.alibaba.fastjson.JSONObject.toJavaObject(json, Map.class);
	}
 */
