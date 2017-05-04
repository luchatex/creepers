package com.fosun.fc.projects.creepers.service;

import java.util.List;

import com.fosun.fc.projects.creepers.entity.TCreepersCreditBlackList;

public interface ICreepersCreditBlackListService extends BaseService {

    public void processByRequest(String taskType);

    public List<TCreepersCreditBlackList> findListByName(String name);

    public void deleteByName(String name);

    public void saveEntity(TCreepersCreditBlackList entity);

    public void saveEntity(List<TCreepersCreditBlackList> entityList);

    public void saveOrUpdate(TCreepersCreditBlackList entity);
}
