package com.fosun.fc.projects.creepers.redis.service.Impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component("redisCacheServiceImpl")
public class RedisCacheServiceImpl<K,V> extends AbstractRedisCacheService<K,V> {
    
    @Autowired
    private RedisTemplate<K,V> redisTemplate;
    
	public void refresh(){}
	
	/**
	 * 指定key的值自增，步长默认为1.
     * @Param redisKey key值
     * @Param delta 步长（增量长度）
     */
    @Override
    public long increment(K redisKey){
        return redisTemplate.opsForValue().increment(redisKey, 1);
    }
	
	/**
	 * 指定key的值自增
     * @Param redisKey key值
     * @Param delta 增量长度（步长）
     */
	@Override
    public long increment(K redisKey, Long delta){
        return redisTemplate.opsForValue().increment(redisKey, delta);
    }
	
    /**
     * 这是一个计数器
     * @Param redisKey key值
     * @Param times 时间的长度
     * @Param unit 时间单位
     */
    @Override
    public void expire(K redisKey, Long times,final TimeUnit unit) {
        redisTemplate.expire(redisKey, times, unit);
    }

}
