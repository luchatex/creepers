package com.fosun.fc.projects.creepers.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dao.CreepersDishonestBaiduDao;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersDishonestBaidu;
import com.fosun.fc.projects.creepers.pageprocessor.baidu.DishonestBaiduProcessor;
import com.fosun.fc.projects.creepers.pipeline.baidu.DishonestBaiduPipeline;
import com.fosun.fc.projects.creepers.service.ICreepersDishonestBaiduService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

@Service
@Transactional
public class CreepersDishonestBaiduServiceImpl implements ICreepersDishonestBaiduService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DishonestBaiduProcessor dishonestBaiduProcessor;
    @Autowired
    private DishonestBaiduPipeline dishonestBaiduPipeline;
    @Autowired
    private CreepersDishonestBaiduDao creepersDishonestBaiduDao;
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Override
    public void processByRequest(String taskType) {
        logger.info("=============>>CreepersDishonestBaiduServiceImpl.processByRequest start!");
        // 查询任务
        int threadNum = 30;
        CreepersParamDTO param = new CreepersParamDTO();
        param.putSearchKeyWord(taskType);
        param.setTaskType(taskType);
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());

        // 启动爬虫
        logger.info("=============>>启动爬虫!");
        Spider spider = Spider.create(dishonestBaiduProcessor).addPipeline(dishonestBaiduPipeline)
                .setDownloader(new DungProxyDownloader().setParam(param)).thread(threadNum).setExitWhenComplete(false);
        // 初始化Request
        for (int i = 0; i < threadNum; i++) {
            // 初始化Request
            Request request = creepersTaskListServiceImpl.popRequest(taskType);
            if (request != null) {
                spider.addRequest(request);
            }
        }
        spider.run();
        logger.info("=============>>CreepersDishonestBaiduServiceImpl.processByRequest end!");

    }

    @Override
    public void saveEntity(TCreepersDishonestBaidu entity) {
        List<TCreepersDishonestBaidu> entityList = creepersDishonestBaiduDao.findByNameAndCertNoAndCaseCode(entity.getName(),entity.getCertNo(),entity.getCaseCode());
        if (null==entityList||entityList.isEmpty()) {
            creepersDishonestBaiduDao.saveAndFlush(entity);
        }
    }

    @Override
    public void saveEntity(List<TCreepersDishonestBaidu> entityList) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<TCreepersDishonestBaidu> findByNameAndCertNo(String name, String certNo) {
        
        return creepersDishonestBaiduDao.findByNameAndCertNo(name,certNo);
    }
}
