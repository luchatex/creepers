package com.fosun.fc.projects.creepers.spider;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.MedicalGMPProcessor;
import com.fosun.fc.projects.creepers.pipeline.CFDA.MedicalPipline;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class SpiderMedicalGMPStartTest extends SpiderBaseTest {

    @Autowired(required = true)
    private MedicalGMPProcessor medicalGMPProcessor;

    @Autowired(required = true)
    private MedicalPipline medicalPipline;


    // GMP
    public void methodTest1() {
        String threadNum = "5";
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.MEDICAL_GMP_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL,MedicalGMPProcessor.INDEX_URL);
        Request request = CommonMethodUtils.buildDefaultRequest(param,"http://app1.sfda.gov.cn/datasearch/face3/base.jsp?tableId=23&tableName=TABLE23&title=GMP%E8%AE%A4%E8%AF%81&bcId=118715589530474392063703010776");
        request.putExtra(BaseConstant.PARAM_EXTRA_CURRENT_DETAIL_NO, "4026");
        request.putExtra(BaseConstant.PARAM_EXTRA_THREAD_NUM, threadNum);
        Spider.create(medicalGMPProcessor).addPipeline(medicalPipline)
                .setDownloader(new DungProxyDownloader().setParam(param))
                .addRequest(request)
                .thread(Integer.valueOf(threadNum)).run();
    }

    @Test
    public void run() {
        methodTest1();
    }

}
