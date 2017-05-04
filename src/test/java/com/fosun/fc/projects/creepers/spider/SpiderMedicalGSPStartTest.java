package com.fosun.fc.projects.creepers.spider;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.MedicalGSPProcessor;
import com.fosun.fc.projects.creepers.pipeline.CFDA.MedicalPipline;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

public class SpiderMedicalGSPStartTest extends SpiderBaseTest {

    @Autowired(required = true)
    private MedicalGSPProcessor medicalGSPProcessor;
    private MedicalPipline medicalPipline;
    
    @Autowired(required = true)
    private DungProxyDownloader dungProxyDownloader;

    public void methodTest1() {
        String threadNum = "1";
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
//        Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue());
        Request request = CommonMethodUtils.buildGetRequestCarryMap("http://qy1.sfda.gov.cn/datasearch/face3/content.jsp?tableId=24&tableName=TABLE24&tableView=GSP认证&Id=113839");
        request.setMethod(HttpConstant.Method.POST);
        Spider.create(medicalGSPProcessor).addPipeline(medicalPipline)
                .setDownloader(dungProxyDownloader.setParam(param))
                .addRequest(request)
                .thread(Integer.valueOf(threadNum)).run();
    }

    @Test
    public void run() {
        methodTest1();
    }

}
