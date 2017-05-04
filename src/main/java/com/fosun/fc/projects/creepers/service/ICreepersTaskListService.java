package com.fosun.fc.projects.creepers.service;

import java.util.Collection;
import java.util.List;

import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersTaskList;
import com.fosun.fc.projects.creepers.entity.TCreepersTaskTemplate;

import us.codecraft.webmagic.Request;

public interface ICreepersTaskListService extends BaseService {

    public void initTaskList(TCreepersTaskTemplate taskTemplate, String startIndex, String endIndex, String session);

    public void saveEntity(TCreepersTaskList entity);

    public void saveEntity(List<TCreepersTaskList> entityList);

    public void updateMedicalTaskList(String url, String flag);

    public void updateMedicalTaskListByProcessType(String taskType, String processType, String flag, long step);

    public List<Object[]> countByTaskType(String taskType);

    public Long countByTaskTypeAndFlag(String taskType, String flag);

    public List<TCreepersTaskList> findByTaskTypeAndFlag(String taskType, String flag);

    public List<TCreepersTaskList> findByTaskTypeAndFlagNot(String taskType, String flag);

    public List<TCreepersTaskList> findByTaskTypeAndFlagIn(String taskType, Collection<String> flagList);

    public void updateFlagByUrl(String url, String flag);
    
    public void updateTaskListFlagByTaskTypeAndFlagIn(String taskType, List<String> flagList, String newFlag, long step);
    
    public void updateTop100000TaskListFlagByTaskTypeAndFlagNotOrderById(String taskType, String flag, String newFlag);
    
    public void updateTaskListFlagByTaskTypeAndFlagNot(String taskType, String flag, String newFlag, long step);

    public void pushRequest(List<TCreepersTaskList> resultList, String processType, long step);

    public Request popRequest(String taskListType);

    public List<TCreepersTaskList> findTop100000ByTaskType(String taskListType);

    public void saveAndFlush(TCreepersTaskList entity);

    public TCreepersTaskList findTop1ByUrl(String url);

    public void deleteByTaskType(String taskType);

    public List<TCreepersTaskList> findTop100000ByTaskTypeAndFlagNotOrderById(String taskListType, String value);

    public void clearRedisCache(String taskType);
    
    public void initTaskList(TCreepersTaskTemplate taskTemplate, long startIndex, long endIndex, String session);

    public CreepersLoginParamDTO popParam(String taskListType);
}
