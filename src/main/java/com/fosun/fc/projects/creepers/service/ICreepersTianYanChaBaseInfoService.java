package com.fosun.fc.projects.creepers.service;

import java.util.List;

import com.fosun.fc.projects.creepers.entity.TCreepersTycBaseInfo;

/**
 * 
 * <p>
 * description: 天眼查工商基础信息Service
 * </p>
 * 
 * @author MaXin
 * @since 2017-2-8 23:49:00
 * @see
 */
public interface ICreepersTianYanChaBaseInfoService extends BaseService {

    public void saveEntity(TCreepersTycBaseInfo entity);
    
    public void processByRequest();

    public List<TCreepersTycBaseInfo> findListByName(String name);
}
