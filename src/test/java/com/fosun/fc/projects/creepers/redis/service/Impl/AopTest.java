package com.fosun.fc.projects.creepers.redis.service.Impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.entity.TCreepersAdminBeijing;
import com.fosun.fc.projects.creepers.service.ICreepersAdminBeijingService;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;

public class AopTest extends SpiderBaseTest {
    @Autowired
    private ICreepersAdminBeijingService creepersAdminBeijingServiceImpl;
    
    @Test
    public void aopTest(){
        TCreepersAdminBeijing entity = null;
        creepersAdminBeijingServiceImpl.saveEntity(entity);
    }
}
