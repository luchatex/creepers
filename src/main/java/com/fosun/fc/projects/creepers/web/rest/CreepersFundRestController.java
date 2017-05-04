package com.fosun.fc.projects.creepers.web.rest;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.modules.utils.JsonResult;
import com.fosun.fc.modules.utils.JsonResult.JSON_RESULT_TYPE;
import com.fosun.fc.modules.web.MediaTypes;
import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;
import com.fosun.fc.projects.creepers.service.ICreepersListFundService;

@RestController
@RequestMapping(value = "/api")
public class CreepersFundRestController extends CreepersBaseRestController {

    @Autowired
    private ICreepersListFundService creepersListFundServiceImpl;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/fund/query", method = RequestMethod.POST, produces = MediaTypes.JSON_UTF_8)
    public ResponseEntity<JsonResult<Map>> queryByParam(@RequestBody String inputJson, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        JsonResult jsonResult = null;
        CreepersLoginParamDTO param = JSONObject.parseObject(inputJson, CreepersLoginParamDTO.class);
        param.setTaskType("fund_list");
        if (StringUtils.isNotBlank(param.getLoginName())&&StringUtils.isNotBlank(param.getPassword())) {
            jsonResult = creepersListFundServiceImpl.queryByParam(param);
            return new ResponseEntity<JsonResult<Map>>(jsonResult, HttpStatus.OK);
        } else {
            jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, "搜索账号或密码，不能为空！",
                    new HashMap<String, Object>());
            return new ResponseEntity<JsonResult<Map>>(jsonResult, HttpStatus.OK);
        }
    }
    
    @RequestMapping(value = "/fund/checkStatus", method = RequestMethod.POST, produces = MediaTypes.JSON_UTF_8)
    public ResponseEntity<JsonResult<Map<String, Object>>> checkStatus(@RequestBody String inputJson, HttpServletRequest request,
            HttpServletResponse response) {
        JsonResult<Map<String, Object>> jsonResult = null;
        CreepersLoginParamDTO param = JSONObject.parseObject(inputJson, CreepersLoginParamDTO.class);
        param.setTaskType("fund_list");
        if (StringUtils.isNotBlank(param.getLoginName())&&StringUtils.isNotBlank(param.getPassword())) {
            jsonResult = creepersListFundServiceImpl.queryStatusByParam(param);
            return new ResponseEntity<JsonResult<Map<String, Object>>>(jsonResult, HttpStatus.OK);
        } else {
            jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, "搜索账号或密码，不能为空！", new HashMap<String, Object>());
            return new ResponseEntity<JsonResult<Map<String, Object>>>(jsonResult, HttpStatus.OK);
        }
    }
}
