package com.fosun.fc.projects.creepers.dao;

import java.util.List;

import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.entity.TCreepersCompanyList;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

public class CompanyListTest extends SpiderBaseTest{
	
    @Autowired
    private CreepersCompanyListDao creepersCompanyListDao;
	
    @Test
    public void run() {
        findListTest();
//        findTest();
//        countTest();
//        addTest();
    }
    
    public void findListTest() {
        System.err.println(System.currentTimeMillis());
        List<String> entityList =creepersCompanyListDao.findByIdGreaterThanAndIdLessThanEqual(10000, 210000);
        System.err.println(System.currentTimeMillis());
        System.err.println(entityList);
    }
    
    public void findTest() {
        int maxId =creepersCompanyListDao.findMaxId();
        System.err.println(maxId);
    }
    
    public void countTest() {
        int count =creepersCompanyListDao.countByIdGreaterThanAndIdLessThanEqual(1000, 1000020);
        System.err.println(count);
    }
    
	public void  addTest() {
	    TCreepersCompanyList entity = new TCreepersCompanyList();
	    entity.setName("test");
	    CommonMethodUtils.setByDT(entity);
	    creepersCompanyListDao.save(entity);
	}
	
}
