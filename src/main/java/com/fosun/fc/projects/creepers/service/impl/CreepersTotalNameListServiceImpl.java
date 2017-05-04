package com.fosun.fc.projects.creepers.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fosun.fc.projects.creepers.dao.CreepersTotalNameListDao;
import com.fosun.fc.projects.creepers.entity.TCreepersTotalNameList;
import com.fosun.fc.projects.creepers.service.ICreepersTotalNameListService;

@Service
@Transactional
public class CreepersTotalNameListServiceImpl implements ICreepersTotalNameListService {
    
    @Autowired
    CreepersTotalNameListDao creepersTotalNameListDao;

    @Override
    public void saveAndUpdateTag(String name, BigDecimal businessTag) {
        List<TCreepersTotalNameList> entityList = creepersTotalNameListDao.findByName(name);
        if(entityList.size()<=0){
            return;
        }
        TCreepersTotalNameList entity = entityList.get(0);
        if(businessTag.longValue()!=(entity.getBusinessTag().longValue() & businessTag.longValue())){
            entity.setBusinessTag(entity.getBusinessTag().add(businessTag));
        }
        creepersTotalNameListDao.save(entity);
    }
}
