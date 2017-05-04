package com.fosun.fc.projects.creepers.service.impl;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dao.CreepersCreditBlackListDao;
import com.fosun.fc.projects.creepers.downloader.CreditChinaDungDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersCreditBlackList;
import com.fosun.fc.projects.creepers.pageprocessor.CreditChina.CreditBlackListProcessor;
import com.fosun.fc.projects.creepers.pipeline.CreditChina.CreditBlacklistPipeline;
import com.fosun.fc.projects.creepers.service.ICreepersCreditBlackListService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

@Service
@Transactional
public class CreepersCreditBlackListServiceImpl implements ICreepersCreditBlackListService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CreditBlackListProcessor creditBlackListProcessor;
    @Autowired
    private CreditBlacklistPipeline creditBlacklistPipeline;
    @Autowired
    private CreepersCreditBlackListDao creepersCreditBlackListDao;
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Override
    public void processByRequest(String taskType) {
        logger.info("=============>>CreepersCreditBlackListServiceImpl.processByRequest start!");
        // 查询任务
        int threadNum = 30;
        CreepersParamDTO param = new CreepersParamDTO();
        param.putSearchKeyWord(taskType);
        param.setTaskType(taskType);
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());

        // 启动爬虫
        logger.info("=============>>启动爬虫!");
        Spider spider = Spider.create(creditBlackListProcessor).addPipeline(creditBlacklistPipeline)
                .setDownloader(new CreditChinaDungDownloader().setParam(param)).thread(threadNum)
                .setExitWhenComplete(false);
        // 初始化Request
        for (int i = 0; i < threadNum; i++) {
            // 初始化Request
            Request request = creepersTaskListServiceImpl.popRequest(taskType);
            if (request != null) {
                spider.addRequest(request);
            }
        }
        spider.run();
        logger.info("=============>>CreepersCreditBlackListServiceImpl.processByRequest end!");
    }

    @Transactional(readOnly = true)
    @Override
    public List<TCreepersCreditBlackList> findListByName(String name) {
        return creepersCreditBlackListDao.findByName(name);
    }

    @Override
    public void deleteByName(String name) {
        creepersCreditBlackListDao.deleteByName(name);
    }

    @Override
    public void saveEntity(TCreepersCreditBlackList entity) {
        creepersCreditBlackListDao.saveAndFlush(entity);
    }

    @Override
    public void saveEntity(List<TCreepersCreditBlackList> entityList) {
        for (TCreepersCreditBlackList entity : entityList) {
            creepersCreditBlackListDao.saveAndFlush(entity);
        }
    }

    @Override
    public void saveOrUpdate(TCreepersCreditBlackList entity) {
        List<TCreepersCreditBlackList> oldEntityList = creepersCreditBlackListDao.findByName(entity.getName());
        if (!CommonMethodUtils.isEmpty(oldEntityList)) {
            entity.setId(oldEntityList.get(0).getId());
            entity.setUpdatedDt(new Date());
            entity.setVersion(oldEntityList.get(0).getVersion());
        }
        creepersCreditBlackListDao.saveAndFlush(entity);
    }
}
