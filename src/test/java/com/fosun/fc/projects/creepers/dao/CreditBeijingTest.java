package com.fosun.fc.projects.creepers.dao;

import java.math.BigDecimal;
import java.util.HashMap;

import org.hyperic.sigar.Tcp;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.entity.TCreepersAdminBeijing;
import com.fosun.fc.projects.creepers.service.ICreepersAdminBeijingService;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.code.BaseCode;

public class CreditBeijingTest extends SpiderBaseTest{
	
	@Autowired
	private CreepersAdminBeijingDao creepersAdminBeijingDao;
	@Autowired(required = true)
	private ICreepersAdminBeijingService creepersAdminBeijingServiceImpl;
	
	@Test
	public void  add() {
//		HashMap<String, String> map = new HashMap<String, String>();
//		map.put("companyName", "北京艾尔方达商贸有限公司");
//		map.put("updateTime", "2016-05-23");
//		map.put("announceInstitute", "北京市工商行政管理局朝阳分局");
//		map.put("announceType", "关于对北京艾尔方达商贸有限公司的处罚决定");
//		try {
//			System.err.println(map.toString());
//			System.err.println(BaseCode.encryptMD5(map.toString().getBytes()).toString());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		int i = 3;
//		int s = i+2;
	    TCreepersAdminBeijing entity = new TCreepersAdminBeijing();
	    entity.setKey("test2");
	    entity.setType("test");
	    java.util.Date date = new java.util.Date();
        entity.setUpdatedBy("admin");
        entity.setCreatedBy("admin");
        entity.setUpdatedDt(date);
        entity.setCreatedDt(date);
	    creepersAdminBeijingServiceImpl.saveOrUpdate(entity);
	}
}
