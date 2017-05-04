package com.fosun.fc.projects.creepers.schedule;

import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersJobDTO;
import com.fosun.fc.projects.creepers.service.ICreepersListService;

@DisallowConcurrentExecution
@Component
public class QuartzJobFactoryDisallowConcurrentExecution extends QuartzJobBean {
    /**
     * Job有状态实现类，不允许并发执行 若一个方法一次执行不完下次轮转时则等待该方法执行完后才执行下一次操作
     * 主要是通过注解：@DisallowConcurrentExecution
     * 
     * @author pengyk
     * 
     */
    Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Value("${job.switch}")
    private String jobSwitch;

    @Autowired
    private ICreepersListService creepersListServiceImpl;
    
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        if("true".equals(jobSwitch)){
            CreepersJobDTO scheduleJob = (CreepersJobDTO) context.getMergedJobDataMap().get("scheduleJob");
            logger.info("运行任务组名称 = [" + scheduleJob.getJobGroup() + "]");
            logger.info("运行任务名称 = [" + scheduleJob.getJobName() + "]");
            if (BaseConstant.TaskListParentType.PUST_REQUEST_TO_REDIS_SEARCH_ALL.equals(BaseConstant.TaskListType.valueOf(StringUtils.upperCase(scheduleJob.getJobGroup())).getType())) {
                creepersListServiceImpl.addTaskByRedisPush(scheduleJob.getJobGroup(),BaseConstant.ProcessByRedisType.WHOLE_DATA.getValue());
            }else {
                creepersListServiceImpl.addTaskByMerName(scheduleJob.getJobGroup(), scheduleJob.getJobName());
            }
        }
    }
}
