package com.fosun.fc.projects.creepers.getResource;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JsonConvertTest {

    private static final String jsonSource = "[{\"keyid\":\"keyid001\",\"cidorcode\":\"3209251987122525x\",\"name\":\"魏云波\",\"datatime\":\"20161010\",\"type\":\"1\",\"t\":\"c\",\"json\":[{\"title\":\"判决文书标题1\",\"anyou\":\"案由1\"},{\"title\":\"判决文书标题2\",\"anyou\":\"案由2\"}]},{\"keyid\":\"keyid001\",\"cidorcode\":\"3209251987122525x\",\"name\":\"魏云波\",\"datatime\":\"20161010\",\"type\":\"2\",\"t\":\"c\",\"json\":[{\"a\":\"判决文书标题1\",\"b\":\"案由1\"},{\"a\":\"判决文书标题2\",\"b\":\"案由2\"}]},{\"keyid\":\"keyid001\",\"cidorcode\":\"3209251987122525x\",\"name\":\"魏云波\",\"datatime\":\"20161010\",\"type\":\"3\",\"t\":\"c\",\"json\":[{\"c\":\"判决文书标题1\",\"d\":\"案由1\"},{\"c\":\"判决文书标题2\",\"d\":\"案由2\"}]}]";

    @SuppressWarnings("unused")
    public static void readJson(String jsonStr) {
        if (StringUtils.isBlank(jsonStr)) {
            return;
        }
        if (jsonStr.startsWith("[")) {
            JSONArray sourceJsonArray = JSONArray.parseArray(jsonStr);
            for (Object object : sourceJsonArray) {
                Set<String> keySet = ((JSONObject) object).keySet();
                for (String key : keySet) {
                    System.out.println("==================>>> JSONArray start!");
                    readJson(((JSONObject) object).toJSONString());
                }
            }
        } else if (jsonStr.startsWith("{")) {
            JSONObject sourceJsonObj = JSONObject.parseObject(jsonStr);
            Set<String> keySet = sourceJsonObj.keySet();
            for (String key : keySet) {
                System.out.println("==================>>> JSONObject start!");
                String value = sourceJsonObj.getString(key);
//                System.out.println(key + ":" + value);
                readJson(value);
            }
        } else {
            System.out.println("==================>>> value start!");
            System.out.println("value : " + jsonStr);
        }
    }
    
    public static void main(String[] args){
        readJson(jsonSource);
    }
}
