package com.fosun.fc.projects.creepers.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fosun.fc.projects.creepers.dao.CreepersBusinessTagDao;
import com.fosun.fc.projects.creepers.entity.TCreepersBusinessTag;
import com.fosun.fc.projects.creepers.service.ICreepersBusinessTagService;

@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class CreepersBusinessTagServiceImpl implements ICreepersBusinessTagService {

    @Autowired
    private CreepersBusinessTagDao creepersBusinessTagDao;
    
    @Override
    public void saveEntity(TCreepersBusinessTag entity) {
        
        creepersBusinessTagDao.saveAndFlush(entity);
    }

    @Override
    public TCreepersBusinessTag findTop1ByTaskType(String taskType) {
        
        return creepersBusinessTagDao.findTop1ByTaskType(taskType);
    }
    
}
