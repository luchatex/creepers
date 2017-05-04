package com.fosun.fc.projects.creepers.spider;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.yellowPage.ShunQiProcessor;
import com.fosun.fc.projects.creepers.pipeline.yellowPage.ShunQiPipline;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class SpiderShunQiStartTest extends SpiderBaseTest {


    @Autowired
    private ShunQiProcessor shunQiProcessor;

    @Autowired
    private ShunQiPipline shunQiPipline;
    
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    
    // 顺企网 黄页信息
    public void methodTest1() {
        String threadNum = "1";
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.COMPANY_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.COMPANY_LIST.getValue());
//        Request request = new Request("http://www.11467.com/beijing/co/747782.htm");
        Spider.create(shunQiProcessor).addPipeline(shunQiPipline)
                .setDownloader(new DungProxyDownloader().setParam(param))
                .addRequest(request)
                .thread(Integer.valueOf(threadNum)).run();
    }

    @Test
    public void run() {
        methodTest1();
    }

}
