package com.fosun.fc.projects.creepers.spider;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.TianYanChaSeleniumDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.TianYanChaProcessor;
import com.fosun.fc.projects.creepers.pipeline.HtmlToJsonFilePipline;
import com.fosun.fc.projects.creepers.pipeline.TianYanChalPipline;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class SpiderTianYanChaBaseInfoStartTest extends SpiderBaseTest {

    @Autowired(required = true)
    private TianYanChaProcessor tianYanChaProcessor;

    @Autowired(required = true)
    private TianYanChalPipline tianYanChalPipline;
    
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    @Autowired(required = true)
    private TianYanChaSeleniumDownloader tianYanChaSeleniumDownloader;
    
    // 天眼查
    public void methodTest1() {
        String threadNum = "10";
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.TYC_BASE_INFO.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        Request request = new Request("http://www.tianyancha.com/search?checkFrom=searchBox&key=安徽省信肯文化传播有限公司");
//        Request request1 = new Request("http://www.tianyancha.com/search?checkFrom=searchBox&key=杭州网易投资有限公司");
        Spider.create(tianYanChaProcessor).addPipeline(tianYanChalPipline)
                .addPipeline(new HtmlToJsonFilePipline())
                .setDownloader(tianYanChaSeleniumDownloader.setParam(param))
//                .addRequest(request)
                .addRequest(creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue()))
                .addRequest(creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue()))
                .addRequest(creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue()))
                .addRequest(creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue()))
                .addRequest(creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue()))
                .addRequest(creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue()))
                .addRequest(creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue()))
                .addRequest(creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue()))
                .addRequest(creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue()))
                .addRequest(creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue()))
                .thread(Integer.valueOf(threadNum)).run();
    }

    @Test
    public void run() {
//        methodTest1();
    }

}
