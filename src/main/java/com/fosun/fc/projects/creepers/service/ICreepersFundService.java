package com.fosun.fc.projects.creepers.service;

import java.util.Map;

import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;

/**
 * 
 * <p>
 * description: 上海公积金 Service
 * </p>
 * 
 * @author pengyk
 * @since 2016年9月07日
 * @see
 */
public interface ICreepersFundService extends BaseService {

    public Map<String, Object> findByLoginNameForMap(String loginName);

	void updateFund(Map<String,Object> map, String taskType, String loginName) throws Exception;
	
	public void processByParam(CreepersLoginParamDTO param);

	public void deleteByLoginName(String loginName);

	public Map<String, Object> findByLoginNameForMapForRest(String loginName) throws Exception;
	
	public Map<String, Object> findByLoginNameForMapFromRedis(CreepersLoginParamDTO param) throws Exception;
}
