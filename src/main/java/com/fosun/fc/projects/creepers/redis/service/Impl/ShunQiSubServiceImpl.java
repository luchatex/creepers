package com.fosun.fc.projects.creepers.redis.service.Impl;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.redis.service.IRedisSubService;
import com.fosun.fc.projects.creepers.service.ICreepersExceptionHandleService;
import com.fosun.fc.projects.creepers.service.ICreepersShunQiService;

public class ShunQiSubServiceImpl implements IRedisSubService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICreepersShunQiService creepersShunQiServiceImpl;
    
    @Autowired
    private ICreepersExceptionHandleService creepersExceptionHandleServiceImpl;

    @Override
    public void handleMessage(Serializable message) {

        logger.info("=============>ShunQiSubServiceImpl.handleMessage start!");
        // 接收消息
        String taskType = message.toString();
        // 增加异常处理
        try {
            creepersShunQiServiceImpl.processByRequest();
            logger.info("=============>ShunQiSubServiceImpl.handmleMessage end!");
        } catch (Exception e) {
            e.printStackTrace();
            // 异常处理
            CreepersParamDTO param = new CreepersParamDTO();
            param.putSearchKeyWord("taskType:"+taskType);
            param.setTaskType(BaseConstant.TaskListType.COMPANY_LIST.getValue());
            param.setErrorPath(e.getCause().getClass() + e.getCause().getMessage());
            param.setErrorInfo(e.getMessage());
            creepersExceptionHandleServiceImpl.handleExceptionAndPrintLog(param);
            logger.error("=============>ShunQiSubServiceImpl.handleMessage end!");
        }

    }

}
