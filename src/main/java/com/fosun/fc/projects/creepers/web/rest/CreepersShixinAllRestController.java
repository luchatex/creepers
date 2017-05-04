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
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersShixinAllDTO;
import com.fosun.fc.projects.creepers.service.ICreepersListService;

@RestController
@RequestMapping(value = "/api")
public class CreepersShixinAllRestController {

    @Autowired
    private ICreepersListService creepersListServiceImpl;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping(value = "/shixinAll/query", method = RequestMethod.POST, produces = MediaTypes.JSON_UTF_8)
    public ResponseEntity<JsonResult<Map>> queryByMerName(@RequestBody String inputJson, HttpServletRequest request,
            HttpServletResponse response) {
        JsonResult jsonResult = null;
        CreepersShixinAllDTO param = JSONObject.parseObject(inputJson, CreepersShixinAllDTO.class);
        if (StringUtils.isNotBlank(param.getName())) {
            jsonResult = creepersListServiceImpl.processByKeyWord(BaseConstant.TaskListType.SHI_XIN_LIST.getValue(),
                    param.getName());
            return new ResponseEntity<JsonResult<Map>>(jsonResult, HttpStatus.OK);
        } else {
            jsonResult = new JsonResult<Map<String, Object>>(JSON_RESULT_TYPE.failure, "搜索关键字，不能为空！",
                    new HashMap<String, Object>());
            return new ResponseEntity<JsonResult<Map>>(jsonResult, HttpStatus.OK);
        }
    }
}
