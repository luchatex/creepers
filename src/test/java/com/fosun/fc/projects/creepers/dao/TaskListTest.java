package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.entity.TCreepersTaskList;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;
import com.google.common.collect.Lists;

public class TaskListTest extends SpiderBaseTest{
	
    @Autowired
    private CreepersTaskListDao creepersTaskListDao;
    
    @Test
    public void  find() {
        List<TCreepersTaskList> count = creepersTaskListDao.findByTaskTypeAndFlagInAndRownum("dishonest_baidu_list_by_name_list", Lists.newArrayList("0","2"), 1000);
        System.err.println(count);
    }
}
