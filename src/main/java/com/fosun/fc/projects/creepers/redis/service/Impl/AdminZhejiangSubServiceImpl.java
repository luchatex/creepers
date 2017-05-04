package com.fosun.fc.projects.creepers.redis.service.Impl;

import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.redis.service.IRedisSubService;
import com.fosun.fc.projects.creepers.service.ICreepersAdminZhejiangService;
import com.fosun.fc.projects.creepers.service.ICreepersExceptionHandleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

public class AdminZhejiangSubServiceImpl implements IRedisSubService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICreepersAdminZhejiangService creepersAdminZhejiangServiceImpl;

    @Autowired
    private ICreepersExceptionHandleService creepersExceptionHandleServiceImpl;

    @Override
    public void handleMessage(Serializable message) {
        
        logger.info("=============>AdminZhejiangSubServiceImpl.handleMessage start!");
        //接收消息
        String taskType = message.toString();
        //增加异常处理
        try {
        	creepersAdminZhejiangServiceImpl.processByRequest(taskType);
            logger.info("=============>AdminZhejiangSubServiceImpl.handleMessage end!");
        } catch (Exception e) {
            e.printStackTrace();
            //异常处理
            CreepersParamDTO paramDTO = new CreepersParamDTO();
            paramDTO.putSearchKeyWord("taskType:"+taskType);
            paramDTO.setTaskType(taskType);
            paramDTO.setErrorPath(e.getCause().getClass() + e.getCause().getMessage());
            String errorInfo = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
            paramDTO.setErrorInfo(errorInfo);
            creepersExceptionHandleServiceImpl.handleExceptionAndPrintLog(paramDTO);
            logger.error("=============>AdminZhejiangSubServiceImpl.handleMessage end!");
        }

    }

}
