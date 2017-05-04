package com.fosun.fc.projects.creepers.schedule;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.alibaba.fastjson.JSON;
import com.fosun.fc.modules.test.category.UnStable;
import com.fosun.fc.modules.test.spring.SpringTransactionalTestCase;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.redis.service.IRedisPubService;
import com.fosun.fc.projects.creepers.redis.service.IRedisScheduleService;
import com.fosun.fc.projects.creepers.schedule.RedisScheduler;
import com.fosun.fc.projects.creepers.service.ICreepersListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;

/**
 * @author pengyk
 */
@Category(UnStable.class)
@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/redis/applicationContext-redis.xml",
        "/schedule/applicationContext-spring-scheduler.xml"})
@TransactionConfiguration(defaultRollback = false)
public class RedisSchedulerTest  extends SpringTransactionalTestCase {

    @Autowired(required = true)
    private ICreepersListService creepersListServiceImpl;
    
    @Autowired(required = true)
    private IRedisScheduleService redisScheduleServiceImpl;
    
    @Autowired(required = true)
    private RedisScheduler redisScheduler;
    
    @Autowired
    private IRedisPubService redisPubService;
    
    @Test
    public void test() {
        RedisScheduler redisScheduler = new RedisScheduler();

        
//        Map<String, String> nameValuePair = new HashMap<>();
//        nameValuePair.put("searchtype", "1");
//        nameValuePair.put("objectType", "2");
//        nameValuePair.put("dataType", "1");
//        nameValuePair.put("exact", "0");
//        nameValuePair.put("keyword", "");
//        nameValuePair.put("areas", "");
//        nameValuePair.put("creditType", "");
//        nameValuePair.put("areaCode", "");
//        nameValuePair.put("templateId", "");
//        nameValuePair.put("page", "1");
//        nameValuePair.put("t", String.valueOf(System.currentTimeMillis()));
//        
//        Request request1 = CommonMethodUtils.buildDefaultRequest(nameValuePair,"http://www.creditchina.gov.cn/publicity_info_search?t="+System.currentTimeMillis());
//        
//        redisScheduler.push("admin_license_list",request1);
        Request request2 = new Request("http://www.creditchina.gov.cn/publicity_info_search?t="+new Date().getTime());
        request2.setMethod("post");
        String jsonMap2 = "{\"areaCode\":\"\",\"searchtype\":\"1\",\"dataType\":\"1\",\"exact\":\"0\",\"areas\":\"\",\"page\":\"2\",\"keyword\":\"\",\"templateId\":\"\",\"creditType\":\"\",\"objectType\":\"2\"}";
        Map<String, Object> map2 = JSON.parseObject(jsonMap2);
        NameValuePair[] nameValuePairs2 = new NameValuePair[map2.size()];
        Set<Entry<String, Object>> entrySet2 = map2.entrySet();
        int index2 = 0;
        for (Entry<String, Object> entry : entrySet2) {
            String value = String.valueOf(entry.getValue())+"";
            nameValuePairs2[index2] = new BasicNameValuePair(entry.getKey(), value);
            index2++;
        }
        request2.putExtra(BaseConstant.POST_NAME_VALUE_PAIR,nameValuePairs2);
        redisScheduler.push("admin_license_list",request2);
        redisPubService.sendMsg("admin_license_list", "admin_license_list");
    }
//    @Test
    public void test2() {
        RedisScheduler redisScheduler = new RedisScheduler();
        redisScheduler.delete("admin_license_list");
    }
    
//    @Test
    public void test3(){
        redisPubService.sendMsg("admin_license_list", "admin_license_list");
    }
    
}
