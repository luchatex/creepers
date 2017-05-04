package com.fosun.fc.projects.creepers.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.fosun.fc.modules.utils.JsonResult;
import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;

public interface ICreepersListService extends BaseService {
    
    /**
     * @param requestType
     *            任务队列类型
     * @param merName
     *            企业名称
     */
    @SuppressWarnings("rawtypes")
    public JsonResult processByMerName(String requestType, String merName);
    @SuppressWarnings("rawtypes")
    public JsonResult processByParam(CreepersLoginParamDTO param) throws Exception;
    
    /**
     * @param requestType
     *            任务队列类型
     * @param merName
     *            企业名称
     */
    @SuppressWarnings("rawtypes")
    public List queryListByKey(String requestType, String key);
    @SuppressWarnings("rawtypes")
    public List queryListByKey(CreepersLoginParamDTO param);
    
    public Page<?> findList(Map<String, Object> searchParams, int pageNumber, int pageSize, String sortType,
            String taskType) throws Exception;
    
    @SuppressWarnings("rawtypes")
    public JsonResult addTaskByMerName(String requestType, String merName);
    
    public void addTaskByRedisPush(String taskListType, String processType);
    @SuppressWarnings("rawtypes")
    public JsonResult addTaskByParam(CreepersLoginParamDTO param);
    public void addTaskByRequestType(String requestType,String wsId);
    
    public void doRecycleByMerName(String requestType, String merName);
    public void doRecycleByParam(CreepersLoginParamDTO param);

    public void insertList(CreepersParamDTO param);
    public void insertList(CreepersLoginParamDTO param);

    public void deleteList(CreepersParamDTO param);
    public void deleteList(CreepersLoginParamDTO param);
    
    public void updateList(CreepersParamDTO param);
    public void updateList(CreepersLoginParamDTO param);

    @SuppressWarnings("rawtypes")
    public List selectList(CreepersParamDTO param);
    @SuppressWarnings("rawtypes")
    public List selectList(CreepersLoginParamDTO param);
    
    /**
     * processByMerName的按需爬取和全量爬取是分离的；processByKeyWord将按需爬取作为全量爬取的有效补充从而紧密结合起来，
     * @param requestType
     *            任务队列类型
     * @param merName
     *            企业名称
     */
    @SuppressWarnings("rawtypes")
    public JsonResult processByKeyWord(String requestType, String keyWord);
    
    /**
     * processFastlyByKeyWord 按需查询的快速通道，查底库没有数据直接new虫子启动爬取任务
     * @param requestType
     *            任务队列类型
     * @param paramMap
     *            查询参数
     */
    @SuppressWarnings("rawtypes")
    public JsonResult processFastlyByMap(String requestType, Map<String, String> paramMap);
}
