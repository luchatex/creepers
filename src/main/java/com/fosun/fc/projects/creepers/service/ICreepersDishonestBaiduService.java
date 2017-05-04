package com.fosun.fc.projects.creepers.service;

import java.util.List;

import com.fosun.fc.projects.creepers.entity.TCreepersDishonestBaidu;

public interface ICreepersDishonestBaiduService extends BaseService {

    public void processByRequest(String taskType);
    
    public void saveEntity(TCreepersDishonestBaidu entity);

    public void saveEntity(List<TCreepersDishonestBaidu> entityList);
    
    List<TCreepersDishonestBaidu> findByNameAndCertNo(String name, String certNo);
}
