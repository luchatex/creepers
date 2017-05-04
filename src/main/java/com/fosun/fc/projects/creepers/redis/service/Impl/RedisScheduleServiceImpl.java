/**
 * 
 */
package com.fosun.fc.projects.creepers.redis.service.Impl;

import org.springframework.stereotype.Service;

import com.fosun.fc.projects.creepers.dto.CreepersTaskListDTO;
import com.fosun.fc.projects.creepers.redis.service.IRedisScheduleService;
import com.fosun.fc.projects.creepers.schedule.RedisScheduler;

import us.codecraft.webmagic.Request;

/**   
*    
* 描述：Redis任务队列Service
* 创建人：pengyk   
* 创建时间：2016年12月15日 上午10:19:19
*    
*/
@Service
public class RedisScheduleServiceImpl implements IRedisScheduleService {
    
    public RedisScheduler redisScheduler = new RedisScheduler();
    
    @Override
    public void pushTask(CreepersTaskListDTO dto) {
        String key = dto.getTaskType();
        Request request = new Request(dto.getUrl());
        redisScheduler.push(key, request);
    }

    @Override
    public Request popTask(String key) {
        Request request = redisScheduler.pop(key);
        return request;
    }

    @Override
    public void delete(String key){
        redisScheduler.delete(key);
    }
}
