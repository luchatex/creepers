package com.fosun.fc.projects.creepers.web;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.modules.test.category.UnStable;
import com.fosun.fc.modules.test.spring.SpringTransactionalTestCase;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dao.CreepersCourtCorpBondsDao;
import com.fosun.fc.projects.creepers.dao.CreepersShixinDao;
import com.fosun.fc.projects.creepers.entity.TCreepersCourtCorpBonds;
import com.fosun.fc.projects.creepers.entity.TCreepersShixin;
import com.fosun.fc.projects.creepers.service.ICreepersCourtCorpBondsService;
import com.fosun.fc.projects.creepers.service.ICreepersShixinService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

@Category(UnStable.class)
@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/redis/applicationContext-redis.xml","/schedule/applicationContext-spring-scheduler.xml" })
@TransactionConfiguration(defaultRollback = false)
public class MakeDataTest extends SpringTransactionalTestCase {

//    @Autowired
//    private CreepersShixinAllDao creepersShixinAllDao;
    @Autowired
    private ICreepersShixinService creepersShixinServiceImpl;
    
    @Autowired
    private CreepersShixinDao creepersShixinDao;

    @Test
    public void test() {
//        TCreepersShixinAll shixinAll = new TCreepersShixinAll();
//        shixinAll.setName("good");
//        shixinAll.setCode("123");
//        shixinAll.setFlag("0");
//        CommonMethodUtils.setByDT(shixinAll);
//        creepersShixinAllDao.save(shixinAll);
//        List<TCreepersCourtCorpBonds> oldList = creepersCourtCorpBondsDao.findByName("吉林成城集团股份有限公司");
//        List<TCreepersCourtCorpBonds> entityList = creepersCourtCorpBondsDao.findByName("吉林成城集团股份有限公司");
//        TCreepersCourtCorpBonds entity = entityList.get(0);
//        System.out.println("MakeDataTest");
//        entity.setProvince("河南");
//        System.out.println("第一次修改");
//        System.out.println(entity.getName());
//        System.out.println(entity.getVersion());
//        System.out.println(entity.getProvince());
//        creepersCourtCorpBondsDao.saveAndFlush(entity);
//        List<TCreepersCourtCorpBonds> entityList1 = creepersCourtCorpBondsDao.findByName("吉林成城集团股份有限公司");
//        TCreepersCourtCorpBonds entity1 = entityList1.get(0);
//        entity1.setProvince("吉林");
//        System.out.println("第二次修改");
//        System.out.println(entity1.getName());
//        System.out.println(entity1.getVersion());
//        System.out.println(entity1.getProvince());
//        creepersCourtCorpBondsDao.saveAndFlush(entity1);
//        creepersShixinServiceImpl.deleteByName("深圳快播");
//        TCreepersShixin entity = new TCreepersShixin();
//        entity.setName("深圳快播");
//        CommonMethodUtils.setByDT(entity);
//        TCreepersShixin entity1 = new TCreepersShixin();
//        entity1.setName("深圳快播1");
//        CommonMethodUtils.setByDT(entity1);
//        List<TCreepersShixin> entityList = new ArrayList<TCreepersShixin>();
//        entityList.add(entity);
//        entityList.add(entity1);
//        creepersShixinServiceImpl.saveEntity(entityList);
//        creepersShixinServiceImpl.deleteAndSave();
        
        TCreepersShixin save1 = new TCreepersShixin();
        save1.setName("save1");
        CommonMethodUtils.setByDT(save1);
        String jsonStr = JSON.toJSONString(save1);
        System.out.println(jsonStr);
    }
}