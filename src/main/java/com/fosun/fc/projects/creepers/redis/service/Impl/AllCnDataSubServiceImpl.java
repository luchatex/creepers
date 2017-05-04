package com.fosun.fc.projects.creepers.redis.service.Impl;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.redis.service.IRedisSubService;
import com.fosun.fc.projects.creepers.service.ICreepersAllCnDataService;
import com.fosun.fc.projects.creepers.service.ICreepersExceptionHandleService;

public class AllCnDataSubServiceImpl implements IRedisSubService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICreepersAllCnDataService creepersAllCnDataServiceImpl;

    @Autowired
    private ICreepersExceptionHandleService creepersExceptionHandleServiceImpl;

    @Override
    public void handleMessage(Serializable message) {
        
        logger.info("=============>AllCnDataSubServiceImpl.handleMessage start!");
        //接收消息
        String taskType = message.toString();
        //增加异常处理
        try {
            creepersAllCnDataServiceImpl.processByRequest(taskType);
            logger.info("=============>AllCnDataSubServiceImpl.handleMessage end!");
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
            logger.error("=============>AllCnDataSubServiceImpl.handleMessage end!");
        }

    }

}
