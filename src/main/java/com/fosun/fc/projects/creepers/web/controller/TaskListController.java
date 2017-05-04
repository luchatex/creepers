package com.fosun.fc.projects.creepers.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.entity.TCreepersTaskTemplate;
import com.fosun.fc.projects.creepers.service.ICreepersListService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskTemplateService;

@Controller
@RequestMapping(value = "/taskList")
public class TaskListController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICreepersTaskListService creepersTasklistServiceImpl;

    @Autowired
    private ICreepersListService creepersListServiceImpl;

    @Autowired
    private ICreepersTaskTemplateService creepersTaskTemplateService;

    /**
     * <p>
     * description:跳转到查询页面
     * </p>
     * 
     * @return
     * @author LiZhanPing
     * @see
     */
    @RequestMapping(value = "/toInitTaskList", method = RequestMethod.GET)
    public String toInitTaskList(Model model) {
        logger.info("========>TaskListController-->toInitTaskList<========");
        model.addAttribute("taskTypeList", BaseConstant.getAllDataTaskListRedis());
        return "taskList/initTaskList";
    }

    @ResponseBody
    @RequestMapping(value = "/initTaskList")
    public Map<String, String> initTaskList(TCreepersTaskTemplate taskTemplate, String startIndex, String endIndex,
            String session) {
        long endIdx = "".equals(endIndex)?0:Long.parseLong(endIndex);
        logger.info("========>TaskListController-->initTaskList<========");
        logger.info("========>任务类型：" + taskTemplate.getTaskType() + "<========");
        logger.info("========>初始化url地址：" + taskTemplate.getUrl() + "<========");
        logger.info("========>请求类型：" + taskTemplate.getHttpType() + "<========");
        logger.info("========>请求参数：" + taskTemplate.getParamMap() + "<========");
        logger.info("========>开始下标：" + startIndex + "<========");
        logger.info("========>结束下标：" + endIndex + "<========");
        logger.info("========>websocket会话ID：" + session + "<========");
        creepersTasklistServiceImpl.initTaskList(taskTemplate, Long.parseLong(startIndex), endIdx, session);
        Map<String, String> result = new HashMap<String, String>();
        result.put("message", "success");
        return result;
    }

    /**
     * <p>
     * description:跳转到查询页面
     * </p>
     * 
     * @return
     * @author LiZhanPing
     * @see
     */
    @RequestMapping(value = "/toResumeTask", method = RequestMethod.GET)
    public String toResumeTask(Model model) {
        logger.info("========>TaskListController-->toResumeTask<========");
        model.addAttribute("taskTypeList", BaseConstant.getAllDataTaskListRedis());
        List<TCreepersTaskTemplate> entityList = creepersTaskTemplateService.findByTaskType("medical_GSP_list");
        TCreepersTaskTemplate entity = entityList.size()<=0?null:entityList.get(0);
        model.addAttribute("taskTemplate", entity);
        return "taskList/resumeTask";
    }

    @ResponseBody
    @RequestMapping(value = "/resumeTask", method = RequestMethod.GET)
    public String resumeTask(Model model, String taskType) {
        logger.info("========>TaskListController-->resumeTask<========");
        creepersListServiceImpl.addTaskByRedisPush(taskType,
                BaseConstant.ProcessByRedisType.BREAK_POINT_DATA.getValue());
        Map<String, String> resultMap = new HashMap<String, String>();
        return JSON.toJSONString(resultMap);
    }

    @ResponseBody
    @RequestMapping(value = "/countTaskByFlag", method = RequestMethod.GET)
    public String countTaskByFlag(Model model, String taskType) {
        logger.info("========>TaskListController-->countTaskByFlag<========");
        List<Object[]> resultList = creepersTasklistServiceImpl.countByTaskType(taskType);
        List<Map<String, Object>> convertList = convertEchart(resultList);
        return JSON.toJSONString(convertList);
    }

    public List<Map<String, Object>> convertEchart(List<Object[]> sourceData) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Object array : sourceData) {
            Object[] objects = (Object[]) array;
            Map<String, Object> map = new HashMap<String, Object>();
            if ("0".equals(objects[0])) {
                map.put("name", "待处理");
            } else if ("1".equals(objects[0])) {
                map.put("name", "处理成功");
            } else if ("2".equals(objects[0])) {
                map.put("name", "处理失败");
            } else if ("3".equals(objects[0])) {
                map.put("name", "处理中");
            } else if ("4".equals(objects[0])) {
                map.put("name", "无效任务");
            } else {
                map.put("name", "其他");
            }
            map.put("value", objects[1]);
            resultList.add(map);
        }
        return resultList;
    }

    @ResponseBody
    @RequestMapping(value = "/clearRedisCache", method = RequestMethod.GET)
    public String clearRedisCache(Model model, String taskType) {
        logger.info("========>TaskListController-->countTaskByFlag<========");
        creepersTasklistServiceImpl.clearRedisCache(taskType);
        Map<String, String> resultMap = new HashMap<String, String>();
        return JSON.toJSONString(resultMap);
    }

    @ResponseBody
    @RequestMapping(value = "/findTaskTemplate", method = RequestMethod.GET)
    public String findTaskTemplate(Model model, String taskType, String type) {
        logger.info("========>TaskListController-->findTaskTemplate<========");
        List<TCreepersTaskTemplate> entityList = creepersTaskTemplateService.findByTaskType(taskType);
        TCreepersTaskTemplate result = entityList.size()<=0?null:entityList.get(0);
        return JSON.toJSONString(result);
    }
}
