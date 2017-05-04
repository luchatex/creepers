package com.fosun.fc.projects.creepers.service;

import java.util.Map;

import com.fosun.fc.modules.utils.JsonResult;
import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;

public interface ICreepersListFundService extends BaseService {
    
    public JsonResult<Map<String, Object>> queryByParam(CreepersLoginParamDTO param) throws Exception;

    public JsonResult<Map<String, Object>> queryStatusByParam(CreepersLoginParamDTO param);

    public void updateStatus(String loginName, String newFlag);
}
