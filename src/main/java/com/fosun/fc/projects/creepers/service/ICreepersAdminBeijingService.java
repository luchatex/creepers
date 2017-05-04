package com.fosun.fc.projects.creepers.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.fosun.fc.projects.creepers.dto.CreepersAdminBeijingDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersAdminBeijing;

public interface ICreepersAdminBeijingService extends BaseService {

    public Page<CreepersAdminBeijingDTO> findList(Map<String, Object> searchParams, int pageNumber, int pageSize,
            String sortType);

    public void processByJob(String JobName);

    void processByRequest(String taskType);
    
    public List<TCreepersAdminBeijing> findByTypeAndKey(String type, String key);

    public void saveEntity(TCreepersAdminBeijing entity);

    public void saveEntity(List<TCreepersAdminBeijing> entityList);
    
    public void saveOrUpdate(TCreepersAdminBeijing entity);

    public void saveOrUpdate(List<TCreepersAdminBeijing> entityList);
}
