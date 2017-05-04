package com.fosun.fc.projects.creepers.dao;

import java.math.BigDecimal;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.entity.TCreepersBusinessTag;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

public class BusinessTagTest extends SpiderBaseTest{
	
	@Autowired
    private CreepersBusinessTagDao creepersBusinessTagDao;
    
    @Test
    public void run(){
        testAdd();
    }
    
    public void  testAdd() {
        TCreepersBusinessTag entity = new TCreepersBusinessTag();
        entity.setTaskType("dishonest_baidu_list_by_total_name_list");
        entity.setBusinessTag(BigDecimal.valueOf(1));
        CommonMethodUtils.setByDT(entity);
        creepersBusinessTagDao.save(entity);
    }
}
