package com.fosun.fc.projects.creepers.utils;

import java.io.FileOutputStream;
import java.util.Properties;

import org.springframework.util.StringUtils;

import com.fosun.fc.modules.utils.PropertiesLoader;

/**
 * 
 * <p>
 * description: 配置文件操作类
 * </p>
 * 
 * @author Pengyk
 * @since 2016年10月10日
 * @see
 */
public class PropertiesUtil {

    private static PropertiesLoader applicationProperties = null;

    private static void loadApplication() {
        applicationProperties = new PropertiesLoader("classpath:/application.properties");
    }

    public static String getApplicationValue(String key) {
        return getApplicationValue(key,"");
    }
    
    public static String getApplicationValue(String key,String defaul) {

        if (applicationProperties == null) {
            loadApplication();
        }
        Object value = applicationProperties.getProperty(key, "");
        if (StringUtils.isEmpty(value)) {
            System.err.println("cannot find the property of " + key + " in the file:" + " application.properties");
            return defaul;
        } else {
            return value.toString().trim();
        }
    }
    
    /**
     * 写入properties信息
     * 
     * @param key
     *            名称
     * @param value
     *            值
     */
    public static void modifyProperties(String key, String value) {
        try {
            // 从输入流中读取属性列表（键和元素对）
            Properties prop = applicationProperties.getProperties();
            prop.setProperty(key, value);
            String path = PropertiesUtil.class.getResource("classpath:/application.properties").getPath();
            FileOutputStream outputFile = new FileOutputStream(path);
            prop.store(outputFile, "modify");
            outputFile.close();
            outputFile.flush();
        } catch (Exception e) {
        }
    }
    
}
