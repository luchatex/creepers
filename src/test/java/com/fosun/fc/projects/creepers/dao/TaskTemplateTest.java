package com.fosun.fc.projects.creepers.dao;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.entity.TCreepersTaskTemplate;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;

public class TaskTemplateTest extends SpiderBaseTest{
	
    @Autowired
    private CreepersTaskTemplateDao creepersTaskTemplateDao;
    
    @Test
    public void  add() {
        List<TCreepersTaskTemplate> entityList = creepersTaskTemplateDao.findByTaskType("dishonest_baidu_list_by_total_name_list");
        TCreepersTaskTemplate entity = new TCreepersTaskTemplate();
        entity.setId(entityList.get(0).getId());
        entity.setTaskType(entityList.get(0).getTaskType());
        entity.setUrl(entityList.get(0).getUrl());
        entity.setHttpType(entityList.get(0).getHttpType());
        entity.setParamMap(entityList.get(0).getParamMap());
        entity.setMemo("6482928");
        entity.setVersion(entityList.get(0).getVersion());
        entity.setUpdatedDt(new Date());
        entity.setUpdatedBy("admin");
        creepersTaskTemplateDao.save(entity);
    }
}
