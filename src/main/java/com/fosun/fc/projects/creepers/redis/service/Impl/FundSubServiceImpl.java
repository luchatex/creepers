package com.fosun.fc.projects.creepers.redis.service.Impl;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;
import com.fosun.fc.projects.creepers.redis.service.IRedisSubService;
import com.fosun.fc.projects.creepers.service.ICreepersExceptionHandleService;
import com.fosun.fc.projects.creepers.service.ICreepersFundService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;

public class FundSubServiceImpl implements IRedisSubService {

    private Logger logger = LoggerFactory.getLogger(getClass());

//    @Autowired
//    private CreepersListFundDao creepersListFundDao;

    @Autowired
    private ICreepersFundService creepersFundServiceImpl;

    @Autowired
    private ICreepersExceptionHandleService creepersExceptionHandleServiceImpl;
    
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Override
    public void handleMessage(Serializable message) {

        logger.info("=============>RedisFundSubServiceImpl.handleMessage start!");
        //接收消息
        String taskType = message.toString();
        // 新增下一个链接任务
        CreepersLoginParamDTO param = creepersTaskListServiceImpl.popParam(taskType);
        // 增加异常处理
        try {
            if (param != null) {
                creepersFundServiceImpl.processByParam(param);
            }
            logger.info("=============>RedisFundSubServiceImpl.handleMessage end!");
        } catch (Exception e) {
            e.printStackTrace();
            // 异常处理
            param.putSearchKeyWord(param.getLoginName());
            param.setTaskType(BaseConstant.TaskListType.FUND_LIST.getValue());
            param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
            param.setErrorPath(e.getCause().getClass() + e.getCause().getMessage());
            param.setErrorInfo(e.getMessage());
            creepersExceptionHandleServiceImpl.handleExceptionAndPrintLog(param);
            logger.error("=============>RedisFundSubServiceImpl.handleMessage end!");
        }

    }

}
