package com.fosun.fc.projects.creepers.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fosun.fc.projects.creepers.dao.CreepersTaskTemplateDao;
import com.fosun.fc.projects.creepers.entity.TCreepersTaskTemplate;
import com.fosun.fc.projects.creepers.service.ICreepersTaskTemplateService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

@Service
public class CreepersTaskTemplateServiceImpl implements ICreepersTaskTemplateService {

    @Autowired
    private CreepersTaskTemplateDao creepersTaskTemplateDao;
    
    @Override
    public List<TCreepersTaskTemplate> findByTaskType(String taskType) {
        
        return creepersTaskTemplateDao.findByTaskType(taskType);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrUpdateEntity(TCreepersTaskTemplate entity) {
        
        if (0 == entity.getId()){
            List<TCreepersTaskTemplate> entityList = creepersTaskTemplateDao.findByTaskType(entity.getTaskType());
            if(CommonMethodUtils.isEmpty(entityList)){
                CommonMethodUtils.setByDT(entity);
            }else {
                entity.setId(entityList.get(0).getId());
                entity.setVersion(entityList.get(0).getVersion());
                entity.setUpdatedDt(new Date());
                entity.setUpdatedBy("admin");
            }
            creepersTaskTemplateDao.saveAndFlush(entity);
        } else {
            entity.setUpdatedDt(new Date());
            entity.setUpdatedBy("admin");
            creepersTaskTemplateDao.saveAndFlush(entity);
        }
    }


}
