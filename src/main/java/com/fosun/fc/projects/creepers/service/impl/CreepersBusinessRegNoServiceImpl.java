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
import com.fosun.fc.projects.creepers.downloader.CreditChinaDungDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CreditChina.BusinessRegNoProcessor;
import com.fosun.fc.projects.creepers.pipeline.CreditChina.BusinessRegNoJsonFilePipline;
import com.fosun.fc.projects.creepers.service.ICreepersBussinessRegNoService;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

/**
 * 
 * <p>
 * description: 信用中国工商注册号查询 Service
 * </p>
 * 
 * @author MaXin
 * @since 2017-3-22 02:18:47
 * @see
 */

@Service
@Transactional
public class CreepersBusinessRegNoServiceImpl implements ICreepersBussinessRegNoService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BusinessRegNoProcessor businessRegNoProcessor;
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Override
    public void processByRequest(String taskType) {
        logger.info("=============>>CreepersBusinessRegNoServiceImpl.processByRequest start!");
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.setTaskType(BaseConstant.TaskListType.BUSINESS_REG_NO.getValue());
        param.putSearchKeyWord(BaseConstant.TaskListType.BUSINESS_REG_NO.getValue());

        int threadNum = 10;
        Spider spider = Spider.create(businessRegNoProcessor).addPipeline(new BusinessRegNoJsonFilePipline())
                .setDownloader(new CreditChinaDungDownloader().setParam(param));
        for (int i = 0; i < threadNum; i++) {
            Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.BUSINESS_REG_NO.getValue());
            if (request != null && null != request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING)) {
                String stringNameValuePair = (String) request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING);
                HashMap<String, String> map = JSON.parseObject(stringNameValuePair, new TypeReference<HashMap<String, String>>() {
                });
                map.put("t", String.valueOf(System.currentTimeMillis()));
                request = CommonMethodUtils.buildDefaultRequest(map, request);
                spider.addRequest(request);
            }
        }
        spider.thread(threadNum).run();
        logger.info("=============>>CreepersBusinessRegNoServiceImpl.processByRequest end!");

    }

}
