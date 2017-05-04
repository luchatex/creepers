package com.fosun.fc.projects.creepers.service;

import com.fosun.fc.projects.creepers.dto.CreepersAdminBeijingDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersAdminBeijing;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ICreepersAdminJiangsuService extends BaseService {

    Page<CreepersAdminBeijingDTO> findList(Map<String, Object> searchParams, int pageNumber, int pageSize,
                                           String sortType);

    void processByJob(String JobName);

    void processByRequest(String taskType);
    
    List<TCreepersAdminBeijing> findByTypeAndKey(String type, String key);

    void saveEntity(TCreepersAdminBeijing entity);

    void saveEntity(List<TCreepersAdminBeijing> entityList);
    
    void saveOrUpdate(TCreepersAdminBeijing entity);

    void saveOrUpdate(List<TCreepersAdminBeijing> entityList);
}
