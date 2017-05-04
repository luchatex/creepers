package com.fosun.fc.projects.creepers.redis.service.Impl;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.entity.TCreepersTaskTemplate;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;

public class InitTaskListTest extends SpiderBaseTest {
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;
    
    @Test
    public void aopTest(){
        
        TCreepersTaskTemplate entity = new TCreepersTaskTemplate();
        entity.setTaskType("dishonest_baidu_list");
        entity.setUrl("https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?resource_id=6899&query=%E5%A4%B1%E4%BF%A1%E4%BA%BA&rn=50&ie=utf-8&oe=utf-8&format=json&pn=");
        entity.setHttpType("get");
        entity.setParamMap("");
        creepersTaskListServiceImpl.initTaskList(entity, "0", "1000", "");
    }
}
