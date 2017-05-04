package com.fosun.fc.projects.creepers.schedule;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;
import com.fosun.fc.projects.creepers.utils.PropertiesUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import us.codecraft.webmagic.Request;

/**
 * 分布式爬虫中，使用Redis作为schecdule来源<br>
 *
 * @author pengyk<br>
 * @since Creepers 1.1.2
 */
@Service
public class RedisScheduler {
    
    Logger logger = LoggerFactory.getLogger(this.getClass());

    protected static JedisPool pool = null;

    private static final String QUEUE_PREFIX = "Queue_";

    private static final String ITEM_PREFIX = "Req_";
    
    private static final String REDIS_IP = "redis.ip";
    
    private static final String REDIS_PORT = "redis.port";
    
    private static final String REDIS_PASSWORD = "redis.password";
    
    private static final String REDIS_TIMEOUT = "redis.timeout";
    
    private static final String HOST = PropertiesUtil.getApplicationValue(REDIS_IP);

    private static final int PORT = Integer.parseInt(PropertiesUtil.getApplicationValue(REDIS_PORT));
    
    private static final String PASSWORD = PropertiesUtil.getApplicationValue(REDIS_PASSWORD);
    
    private static final int TIMEOUT = Integer.parseInt(PropertiesUtil.getApplicationValue(REDIS_TIMEOUT));
    
    private static void initRedisConfig() {
        pool = new JedisPool(new JedisPoolConfig(), HOST, PORT, TIMEOUT, PASSWORD);
    }

    public RedisScheduler() {
        initRedisConfig();
    }
    public void push(String key,Request request) {
        if(pool==null){
            initRedisConfig();
        }
        Jedis jedis = pool.getResource();
        try {
            jedis.rpush(getQueueKey(key), request.getUrl());
            //if (request.getExtras() != null) {
                String field = DigestUtils.sha1Hex(request.getUrl());
                String value = JSON.toJSONString(request);
                jedis.hset((ITEM_PREFIX + key), field, value);
            //}
        } finally {
            pool.returnResource(jedis);
        }
    }

    public void push(String key, List<Request> reqList) {
        if (pool == null) {
            initRedisConfig();
        }
        Jedis jedis = pool.getResource();
        try {
            Pipeline p = jedis.pipelined();
            if (reqList != null && !reqList.isEmpty()) {
                for (Request request : reqList) {
                    if(null!=request){
                        p.rpush(getQueueKey(key), request.getUrl());
                        String field = DigestUtils.sha1Hex(request.getUrl());
                        String value = JSON.toJSONString(request);
                        p.hset((ITEM_PREFIX + key), field, value);
                    } else {
                        logger.info("request is null");
                    }
                }
            }
            p.sync();
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    public synchronized Request pop(String key) {
        if(pool==null){
            initRedisConfig();
        }
        Jedis jedis = pool.getResource();
        try {
            String url = jedis.lpop(getQueueKey(key));
            if (url == null) {
                return null;
            }
            String itemkey = ITEM_PREFIX + key;
            String field = DigestUtils.sha1Hex(url);
            byte[] bytes = jedis.hget(itemkey.getBytes(), field.getBytes());
            if (bytes != null) {
                Request o = JSON.parseObject(new String(bytes), Request.class);
                jedis.hdel(itemkey.getBytes(), field.getBytes());
                return o;
            }
            Request request = new Request(url);
            return request;
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    public void pushParam(String key,CreepersLoginParamDTO loginParam) throws Exception {
        if(pool==null){
            initRedisConfig();
        }
        Jedis jedis = pool.getResource();
        try {
            String value = JSON.toJSONString(loginParam,BaseConstant.features);
            jedis.rpush(key, value);
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    public synchronized CreepersLoginParamDTO popParam(String key) {
        if(pool==null){
            initRedisConfig();
        }
        Jedis jedis = pool.getResource();
        try {
            String encryptStr = jedis.lpop(key);
            String value = encryptStr;
            if (value == null) {
                return null;
            }
            return JSONObject.parseObject(value,CreepersLoginParamDTO.class);
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    protected static String getQueueKey(String key) {
        return QUEUE_PREFIX + key;
    }

    protected static String getItemKey(String key)
    {
        return ITEM_PREFIX + key;
    }

    public int getLeftRequestsCount(String key) {
        Jedis jedis = pool.getResource();
        try {
            Long size = jedis.llen(getQueueKey(key));
            return size.intValue();
        } finally {
            pool.returnResource(jedis);
        }
    }
    
    public void delete(String key) {
        if(pool==null){
            initRedisConfig();
        }
        Jedis jedis = pool.getResource();
        try {
              jedis.del(getQueueKey(key));
              jedis.del(getItemKey(key));
        } finally {
            pool.returnResource(jedis);
        }
    }    

}
