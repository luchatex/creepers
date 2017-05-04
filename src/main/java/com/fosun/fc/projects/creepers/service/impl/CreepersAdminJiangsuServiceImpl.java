package com.fosun.fc.projects.creepers.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fosun.fc.projects.creepers.annotation.IsTryAgain;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dao.CreepersAdminBeijingDao;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersAdminBeijingDTO;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.entity.TCreepersAdminBeijing;
import com.fosun.fc.projects.creepers.pageprocessor.CreditChina.AdminJiangsuProcessor;
import com.fosun.fc.projects.creepers.pipeline.CreditChina.AdminJiangsuPiepline;
import com.fosun.fc.projects.creepers.service.ICreepersAdminJiangsuService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

@Service
@Transactional
public class CreepersAdminJiangsuServiceImpl implements ICreepersAdminJiangsuService{

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private AdminJiangsuProcessor adminJiangsuProcessor;
	
	@Autowired
	private AdminJiangsuPiepline adminJiangsuPiepline;
	
	@Autowired
	private CreepersAdminBeijingDao creepersAdminBeijingDao;
	
	@Autowired
	private ICreepersTaskListService creepersTaskListServiceImpl;
	
	@Override
	public Page<CreepersAdminBeijingDTO> findList(Map<String, Object> searchParams, int pageNumber, int pageSize,
			String sortType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processByJob(String JobName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processByRequest(String taskType) {
		
		int threadNum = 30;
		
		CreepersParamDTO param = new CreepersParamDTO();
        param.putSearchKeyWord(taskType);
        param.setTaskType(taskType);
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        
		// 启动爬虫
        logger.info("=============>>启动爬虫!");
        Spider spider = Spider.create(adminJiangsuProcessor).addPipeline(adminJiangsuPiepline)
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
	}

	@Override
	public List<TCreepersAdminBeijing> findByTypeAndKey(String type, String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveEntity(TCreepersAdminBeijing entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void saveEntity(List<TCreepersAdminBeijing> entityList) {
		// TODO Auto-generated method stub
	}

	@Override
	@IsTryAgain
	public void saveOrUpdate(TCreepersAdminBeijing entity) {
		List<TCreepersAdminBeijing> oldEntityList = creepersAdminBeijingDao.findByKey(entity.getKey());
		if (!CommonMethodUtils.isEmpty(oldEntityList)){
			entity.setId(oldEntityList.get(0).getId());
			entity.setUpdatedDt(new Date());
			entity.setVersion(oldEntityList.get(0).getVersion());
		}
		creepersAdminBeijingDao.saveAndFlush(entity);
	}

	@IsTryAgain
	@Override
	public void saveOrUpdate(List<TCreepersAdminBeijing> entityList) {
		for(int i=0;i<entityList.size();i++){
			saveOrUpdate(entityList.get(i));
		}
	}
	
}
