package com.fosun.fc.projects.creepers.service;

import java.util.List;

import com.fosun.fc.projects.creepers.entity.TCreepersTaskTemplate;

public interface ICreepersTaskTemplateService extends BaseService {

    public List<TCreepersTaskTemplate> findByTaskType(String taskType);
    
    public void saveOrUpdateEntity(TCreepersTaskTemplate entity);
}
