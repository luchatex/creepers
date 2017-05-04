package com.fosun.fc.projects.creepers.service;

import java.math.BigDecimal;

import com.fosun.fc.projects.creepers.entity.TCreepersCompanyList;

/**
 * 
 * <p>
 * description: 顺企网黄页信息 Service
 * </p>
 * 
 * @author MaXin
 * @since 2017-2-10 13:45:37
 * @see
 */
public interface ICreepersShunQiService extends BaseService {

    public void saveEntity(TCreepersCompanyList entity);

    public void processByRequest();

    public void saveAndUpdateTag(String name, BigDecimal businessTag);
}
