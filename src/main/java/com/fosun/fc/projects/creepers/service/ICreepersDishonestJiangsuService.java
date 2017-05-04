package com.fosun.fc.projects.creepers.service;

import com.fosun.fc.projects.creepers.entity.TCreepersDishonestBaidu;

import java.util.List;

public interface ICreepersDishonestJiangsuService extends BaseService {

    public void processByRequest(String taskType);
    
    public void saveEntity(TCreepersDishonestBaidu entity);

    public void saveEntity(List<TCreepersDishonestBaidu> entityList);
    
    List<TCreepersDishonestBaidu> findByNameAndCertNo(String name, String certNo);
}
