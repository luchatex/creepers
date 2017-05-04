package com.fosun.fc.projects.creepers.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.fosun.fc.projects.creepers.dto.CreepersAdminDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersAdmin;

public interface ICreepersAdminService extends BaseService {

    public Page<CreepersAdminDTO> findList(Map<String, Object> searchParams, int pageNumber, int pageSize,
            String sortType);

    public void processByJob(String JobName);

    void processByRequest(String taskType);
    
    public List<TCreepersAdmin> findByTypeAndKey(String type, String key);

    public void saveEntity(TCreepersAdmin entity);

    public void saveEntity(List<TCreepersAdmin> entityList);
    
    public void saveOrUpdate(TCreepersAdmin entity);

    public void saveOrUpdate(List<TCreepersAdmin> entityList);
}
