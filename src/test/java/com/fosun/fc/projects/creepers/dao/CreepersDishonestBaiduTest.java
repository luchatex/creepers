package com.fosun.fc.projects.creepers.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.entity.TCreepersDishonestBaidu;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

public class CreepersDishonestBaiduTest extends SpiderBaseTest{

    
    @Autowired
    private CreepersDishonestBaiduDao creepersDishonestBaiduDao;
    
    @Test
    public void test(){
        TCreepersDishonestBaidu entity = new TCreepersDishonestBaidu();
        entity.setName("zhongguo");
        CommonMethodUtils.setByDT(entity);
        creepersDishonestBaiduDao.saveAndFlush(entity);
                
    }
    
}
