package com.fosun.fc.projects.creepers.web.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fosun.fc.projects.creepers.utils.PropertiesUtil;
import com.fosun.fc.projects.creepers.utils.SystemInfoUtil;

@Controller
@RequestMapping(value = "/monitor")
public class MonitorController {

    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public String index() {
        return "monitor/demo";
    }

    @RequestMapping(value = "/echo", method = RequestMethod.GET)
    public String echo() {
        return "monitor/echo";
    }

    @RequestMapping(value = "/chat", method = RequestMethod.GET)
    public String chat() {
        return "monitor/chat";
    }

    @RequestMapping(value = "/snake", method = RequestMethod.GET)
    public String snake() {
        return "monitor/snake";
    }

    @RequestMapping(value = "/toPerformanceMonitor", method = RequestMethod.GET)
    public String toPerformanceMonitor(Model model) {
        model.addAttribute("cpu", PropertiesUtil.getApplicationValue("cpu","80"));
        model.addAttribute("jvm", PropertiesUtil.getApplicationValue("jvm","80"));
        model.addAttribute("ram", PropertiesUtil.getApplicationValue("ram","80"));
        model.addAttribute("toEmail", PropertiesUtil.getApplicationValue("toEmail","lizhanping@fosun.com"));
        model.addAttribute("systemInfo", SystemInfoUtil.SystemProperty());
        return "monitor/performanceMonitor";
    }
    
    /**
    * 修改配置　
    * @param request
    * @param nodeId
    * @return
    * @throws Exception
    */
   @ResponseBody
   @RequestMapping("/modifySer")
   public Map<String, Object> modifySer(String key,String value) throws Exception{
       Map<String, Object> dataMap = new HashMap<String,Object>();
       try {
       // 从输入流中读取属性列表（键和元素对）
           PropertiesUtil.modifyProperties(key, value);
       } catch (Exception e) {
           dataMap.put("flag", false);
       }
       dataMap.put("flag", true);
       return dataMap;
   }
}
