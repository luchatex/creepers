package com.fosun.fc.projects.creepers.redis.service;

import com.fosun.fc.projects.creepers.dto.CreepersTaskListDTO;
import com.fosun.fc.projects.creepers.service.BaseService;

import us.codecraft.webmagic.Request;

/**
 * 
 * <p>
 * description: Redis任务队列Service
 * </p>
 * 
 * @author Pengyk
 * @since 2016年12月15日
 * @see
 */
public interface IRedisScheduleService extends BaseService {
    //生产任务队列
    public void pushTask(CreepersTaskListDTO dto);
    //消费任务队列
    public Request popTask(String key);
    
    public void delete(String key);
}
