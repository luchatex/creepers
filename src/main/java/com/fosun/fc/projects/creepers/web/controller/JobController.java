package com.fosun.fc.projects.creepers.web.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fosun.fc.modules.mapper.BeanMapper;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersJobDTO;
import com.fosun.fc.projects.creepers.service.ICreepersJobService;
import com.fosun.fc.projects.creepers.utils.TranslateMethodUtil;

@Controller
@RequestMapping("/job")
public class JobController extends BaseController {

    @Autowired
    private ICreepersJobService creepersjobServiceImpl;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String init(Model model) {
        logger.info("========>jobController-->init<========");
        List<Map<String,String>> taskTypeList = BaseConstant.getAllDataTaskListTypeList();
        taskTypeList.addAll(BaseConstant.getAllDataTaskListRedis());
        model.addAttribute("taskTypeList", taskTypeList);
        return "job/jobList";
    }

    @RequestMapping(value = "/initDetail", method = RequestMethod.GET)
    public String initDetail(Model model) {
        logger.info("========>jobController-->init detail<========");
        model.addAttribute("taskTypeList", BaseConstant.getAllDataTaskListTypeList());
        return "job/jobDetail";
    }

    @RequestMapping(value = "/toAdd", method = RequestMethod.GET)
    public String toAdd(String searchJobName, String searchJobGroup, String searchStatus, Model model) {
        logger.info("========>jobController-->to add<========");
        model.addAttribute("searchJobName", searchJobName);
        model.addAttribute("searchJobGroup", searchJobGroup);
        model.addAttribute("searchStatus", searchStatus);
        List<Map<String,String>> taskTypeList = BaseConstant.getAllDataTaskListTypeList();
        taskTypeList.addAll(BaseConstant.getAllDataTaskListRedis());
        model.addAttribute("taskTypeList", taskTypeList);
        return "job/jobAdd";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/list")
    public String list(@RequestParam(value = "page", defaultValue = "1") int pageNumber,
            @RequestParam(value = "page.size", defaultValue = "10") int pageSize,
            @RequestParam(value = "sortType", defaultValue = "auto") String sortType, CreepersJobDTO jobDTO,
            @ModelAttribute("jobName") String jobName, @ModelAttribute("jobGroup") String jobGroup,
            @ModelAttribute("status") String status, Model model) throws Exception {
        logger.info("========>jobController-->list<========");
        if (StringUtils.isNoneBlank(jobName)) {
            jobDTO.setJobName(jobName);
        }
        if (StringUtils.isNoneBlank(jobGroup)) {
            jobDTO.setJobGroup(jobGroup);
        }
        if (StringUtils.isNoneBlank(status)) {
            jobDTO.setStatus(status);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        BeanMapper.copy(jobDTO, map);
        map = removeNullOrDefaultValue(map);
        Map<String, Object> searchParams = TranslateMethodUtil.pageMapToEqSearchMap(map);
        logger.info("========>查询参数：" + searchParams + "<========");
        Page<CreepersJobDTO> resultList = (Page<CreepersJobDTO>) creepersjobServiceImpl.findList(searchParams,
                pageNumber, pageSize, sortType);
        model.addAttribute("resultList", resultList);
        model.addAllAttributes(BeanMapper.toMap(jobDTO));
        model.addAllAttributes(TranslateMethodUtil.buildPageSearchParamMap(map));
        model.addAttribute("taskTypeList", BaseConstant.getAllDataTaskListTypeList());
        return "job/jobList";
    }

    @RequestMapping(value = "/queryJob")
    public String update(Model model, String jobName, String jobGroup, String searchJobName, String searchJobGroup,
            String searchStatus) {
        logger.info("========>jobController-->queryJob<========");
        logger.info("========>查询参数jobName：" + jobName + "<========");
        CreepersJobDTO creepersJobDTO = new CreepersJobDTO();
        try {
            creepersJobDTO = creepersjobServiceImpl.getJob(jobName, jobGroup);
            if (StringUtils.isNotBlank(creepersJobDTO.getIndexUrl()))
                creepersJobDTO.setIndexUrl(creepersJobDTO.getIndexUrl().replace("\"", "&quot;"));
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        model.addAllAttributes(BeanMapper.toMap(creepersJobDTO));
        model.addAttribute("searchJobName", searchJobName);
        model.addAttribute("searchJobGroup", searchJobGroup);
        model.addAttribute("searchStatus", searchStatus);
        return "job/jobAdd";
    }

    @RequestMapping(value = "/doSave", method = RequestMethod.GET)
    public String saveAndUpdateInfo(Model model, CreepersJobDTO creepersJobDTO, String searchJobName,
            String searchJobGroup, String searchStatus, RedirectAttributes redirectAttributes) {
        model.addAllAttributes(BeanMapper.toMap(creepersJobDTO));
        try {
            creepersjobServiceImpl.addJob(creepersJobDTO);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("jobName", searchJobName);
        redirectAttributes.addFlashAttribute("jobGroup", searchJobGroup);
        redirectAttributes.addFlashAttribute("status", searchStatus);
        return "redirect:/job/list";
    }

    @ResponseBody
    @RequestMapping(value = "/doPause")
    public Map<String, String> doPause(String jobName, String jobGroup) throws Exception {
        logger.info("========>jobController-->doPause<========");
        logger.info("========>暂停Job：" + jobName + "<========");
        CreepersJobDTO creepersJobDTO = new CreepersJobDTO();
        creepersJobDTO.setJobName(jobName);
        creepersJobDTO.setJobGroup(jobGroup);
        creepersjobServiceImpl.pauseJob(creepersJobDTO);
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("message", "success");
        return resultMap;
    }

    @ResponseBody
    @RequestMapping(value = "/doResume")
    public Map<String, String> doResume(String jobName, String jobGroup) throws Exception {
        logger.info("========>jobController-->doResume<========");
        logger.info("========>恢复Job：" + jobName + "<========");
        CreepersJobDTO creepersJobDTO = new CreepersJobDTO();
        creepersJobDTO.setJobName(jobName);
        creepersJobDTO.setJobGroup(jobGroup);
        creepersjobServiceImpl.resumeJob(creepersJobDTO);
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("message", "success");
        return resultMap;
    }

    @ResponseBody
    @RequestMapping(value = "/doDelete")
    public Map<String, String> doDelete(String jobName, String jobGroup) throws Exception {
        logger.info("========>jobController-->doDelete<========");
        logger.info("========>删除Job：" + jobName + "<========");
        CreepersJobDTO creepersJobDTO = new CreepersJobDTO();
        creepersJobDTO.setJobName(jobName);
        creepersJobDTO.setJobGroup(jobGroup);
        creepersjobServiceImpl.deleteJob(creepersJobDTO);
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("message", "success");
        return resultMap;
    }

    @ResponseBody
    @RequestMapping(value = "/resumeFromBreakPoint")
    public Map<String, String> resumeFromBreakPoint(String jobName, String jobGroup) throws Exception {
        logger.info("========>jobController-->doResume<========");
        logger.info("========>恢复Job：" + jobName + "<========");
        creepersListServiceImpl.addTaskByMerName(jobGroup, jobName);
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("message", "success");
        return resultMap;
    }

    public Map<String, Object> removeNullOrDefaultValue(Map<String, Object> map) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Set<Entry<String, Object>> entrySet = map.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            if (StringUtils.isNotBlank(String.valueOf(entry.getValue()))
                    && !"null".equals(String.valueOf(entry.getValue()))
                    && !"0".equals(String.valueOf(entry.getValue()))) {
                resultMap.put(entry.getKey(), entry.getValue());
            }
        }
        return resultMap;
    }
}
