package com.fosun.fc.projects.creepers.service;

import com.fosun.fc.projects.creepers.entity.TCreepersBusinessTag;

public interface ICreepersBusinessTagService extends BaseService {

    public void saveEntity(TCreepersBusinessTag entity);

    public TCreepersBusinessTag findTop1ByTaskType(String taskType);
}
