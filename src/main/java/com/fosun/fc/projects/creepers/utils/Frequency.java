package com.fosun.fc.projects.creepers.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.fosun.fc.projects.creepers.redis.service.Impl.AbstractRedisCacheService;

/*
 * 频率控制器
 */
@Component
public class Frequency {

    @Autowired
    private AbstractRedisCacheService<String, Long> redisCacheServiceImpl;
    
    @SuppressWarnings("rawtypes")
    public void frequency(String className, String methodName, Class[] parameterTypes, Object[] parameterValues,
            String maxTimeInterVal, String maxTimes, String taskType, String exceptionName) {
        String maxTI = StringUtils.isBlank(PropertiesUtil.getApplicationValue(maxTimeInterVal)) ? "0"
                : PropertiesUtil.getApplicationValue(maxTimeInterVal);
        String maxTs = StringUtils.isBlank(PropertiesUtil.getApplicationValue(maxTimes)) ? "0"
                : PropertiesUtil.getApplicationValue(maxTimes);
        String redisKey = taskType + "|" + exceptionName;
        long count = redisCacheServiceImpl.increment(redisKey);
        if(count==1){
            redisCacheServiceImpl.expire(redisKey, Long.valueOf(maxTI), TimeUnit.SECONDS);
        }
        if (count <= Long.valueOf(maxTs)) {
            reflectInvoke(className, methodName, parameterTypes, parameterValues);
        }
    }
    
    /*
     * 反射调用
     */
    @SuppressWarnings("rawtypes")
    public static Object reflectInvoke(String className, String methodName, Class[] parameterTypes,
            Object[] parameterValues) {

        Class<?> clazz = null;
        Object object = null;
        try {
            clazz = Class.forName(className);
            // 手动获取spring容器的对象
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            className = toLowerCaseFirstOne(className.substring(className.lastIndexOf(".") + 1));
            object = wac.getBean(className);
            // 原始方法：new一个对象
            // object = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        clazz = object.getClass();
        Method method = null;
        Object result = null;
        try {
            method = clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        try {
            result = method.invoke(object, parameterValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return result;
    }


    public static String toLowerCaseFirstOne(String s) {
        char[] cs = s.toCharArray();
        if (cs[0] < 91 && cs[0] > 64)
            cs[0] += 32;
        return String.valueOf(cs);
    }
}
