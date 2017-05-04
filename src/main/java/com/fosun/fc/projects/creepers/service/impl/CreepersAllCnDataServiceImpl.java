package com.fosun.fc.projects.creepers.service.impl;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.allCnData.AllChinaDataProcessor;
import com.fosun.fc.projects.creepers.pipeline.allCnData.AllCnDataJsonFilePipline;
import com.fosun.fc.projects.creepers.service.ICreepersAllCnDataService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

@Service
@Transactional
public class CreepersAllCnDataServiceImpl implements ICreepersAllCnDataService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AllChinaDataProcessor allChinaDataProcessor;
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Override
    public void processByRequest(String taskType) {
        logger.info("=============>>CreepersAllCnDataServiceImpl.processByRequest start!");
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT .getValue());
        param.setTaskType(BaseConstant.TaskListType.ALL_CHINA_DATA.getValue());
        param.putSearchKeyWord(BaseConstant.TaskListType.ALL_CHINA_DATA.getValue());

        int threadNum = 10;
        Spider spider = Spider.create(allChinaDataProcessor).addPipeline(new AllCnDataJsonFilePipline())
                .setDownloader(new DungProxyDownloader().setParam(param));
        for (int i = 0; i < threadNum; i++) {
            Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.ALL_CHINA_DATA.getValue());
            if (request != null && null != request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING)) {
                String stringNameValuePair = (String) request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING);
                HashMap<String, String> map = JSON.parseObject(stringNameValuePair, new TypeReference<HashMap<String, String>>() {
                });
                request = CommonMethodUtils.buildDefaultRequest(map, request);
                spider.addRequest(request);
            }
        }
        spider.thread(threadNum).run();
        logger.info("=============>>CreepersAllCnDataServiceImpl.processByRequest end!");

    }

}
