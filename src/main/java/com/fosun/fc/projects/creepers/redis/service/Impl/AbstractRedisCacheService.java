package com.fosun.fc.projects.creepers.redis.service.Impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.fosun.fc.projects.creepers.redis.service.IRedisCacheService;

public abstract class AbstractRedisCacheService<K, V> implements IRedisCacheService<K, V> {
	
    @Autowired
    private RedisTemplate<K,V> redisCacheTemplate;
    
    public void set(K key,V value){
        ValueOperations<K, V> vop = (ValueOperations<K, V>) redisCacheTemplate.opsForValue();
        vop.set(key, value);
    
	}
	
	public V get(K key){
	    ValueOperations<K, V> vop = (ValueOperations<K, V>) redisCacheTemplate.opsForValue();
	    return vop.get(key);
	}
	
	public void del(K key){
	    redisCacheTemplate.delete(key);
	}
	
	public abstract void refresh();
	
	public abstract long increment(K redisKey);
	
	public abstract long increment(K redisKey, Long delta);
	
	public abstract void expire(K redisKey, Long times,final TimeUnit unit);
}
