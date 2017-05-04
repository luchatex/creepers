package com.fosun.fc.projects.creepers.redis.service.Impl;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.fosun.fc.modules.test.category.UnStable;
import com.fosun.fc.modules.test.spring.SpringTransactionalTestCase;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.redis.service.IRedisPubService;
import com.fosun.fc.projects.creepers.service.ICreepersListService;

@Category(UnStable.class)
@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/redis/applicationContext-redis.xml",
        "/schedule/applicationContext-spring-scheduler.xml" })
@TransactionConfiguration(defaultRollback = false)
public class RedisTest extends SpringTransactionalTestCase {


    @SuppressWarnings("unused")
    @Autowired(required = true)
    private RedisConfigServiceImpl redisConfigService;

    @Autowired
    private ICreepersListService creepersListServiceImpl;
    
    @Autowired
    private IRedisPubService redisPubService;
    
    @Test
    public void test(){
        //启动爬虫通知
//        tsetSendMsg();
        // 断点续爬
        testBreakDown();
        // 全量爬取
//        testAll();
//        testScheduke();
    }
    
    public void tsetSendMsg(){
        redisPubService.sendMsg("company_list", "company_list");
    }
    
    public void testBreakDown() {
        creepersListServiceImpl.addTaskByRedisPush("company_list",
                BaseConstant.ProcessByRedisType.BREAK_POINT_DATA.getValue());
    }

    public void testAll() {
        creepersListServiceImpl.addTaskByRedisPush("company_list",
                BaseConstant.ProcessByRedisType.WHOLE_DATA.getValue());
    }
    
    public void testAdd() {
//    	UserDTO dto = new UserDTO();
//    	dto.setUserName("mary");
//    	dto.setBirthday(new Date());
//    	dto.setEmail("123123@163.com");
//    	dto.setSex("M");
//    	dto.setCreatedBy("admin");
//    	dto.setCreatedDt(new Date());
//    	dto.setUpdatedBy("admin");
//    	dto.setUpdatedDt(new Date());
//    	redisConfigService.set(dto.getUserName(), dto);
//    	UserDTO dto2 = redisConfigService.get(dto.getUserName());
//    	System.out.println("=========1========="+dto2.getEmail());  
    	/*redisCacheTemplate.opsForSet().add(dto.getUserName(), dto);
    	UserDTO dto2 = redisCacheTemplate.opsForSet().pop(dto.getUserName());
    	System.out.println("=========1========="+dto2.getEmail());  	
    	redisCacheTemplate.opsForSet().remove("samuel", dto);*/
    }

    public void testRemove() {/*
    	UserDTO dto = new UserDTO();
    	dto.setUserName("samuel");
    	dto.setBirthday(new Date());
    	dto.setEmail("jackiepon@163.com");
    	dto.setSex("M");
    	dto.setCreatedBy("admin");
    	dto.setCreatedDt(new Date());
    	dto.setUpdatedBy("admin");
    	dto.setUpdatedDt(new Date());
    	redisCacheTemplate.opsForSet().remove("samuel", dto);
    	UserDTO dto2 = redisCacheTemplate.opsForSet().pop(dto.getUserName());
    	System.out.println("=========1========="+dto2.getEmail());  		
    */}

    
    public void testScheduke(){
        creepersListServiceImpl.addTaskByRedisPush("admin_license_list", "0");
    }
}
