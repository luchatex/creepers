package com.fosun.fc.projects.creepers.spider;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.MedicalInstrumentForeignProcessor;
import com.fosun.fc.projects.creepers.pipeline.CFDA.MedicalPipline;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class SpiderMedicalInstrumentStartTest2 extends SpiderBaseTest {

    @Autowired(required = true)
    private MedicalInstrumentForeignProcessor medicalInstrumentForeignProcessor;

    @Autowired(required = true)
    private MedicalPipline medicalPipline;

    // 进口器械
    public void methodTest2() {
        String threadNum = "5";
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, MedicalInstrumentForeignProcessor.INDEX_URL);
        Request request = CommonMethodUtils.buildDefaultRequest(param, MedicalInstrumentForeignProcessor.INDEX_URL);
        request.putExtra(BaseConstant.PARAM_EXTRA_CURRENT_DETAIL_NO, "17537");
        request.putExtra(BaseConstant.PARAM_EXTRA_THREAD_NUM, threadNum);

        Spider.create(medicalInstrumentForeignProcessor).addPipeline(medicalPipline)
                .setDownloader(new DungProxyDownloader().setParam(param))
                .addRequest(request)
                .thread(Integer.valueOf(threadNum)).run();
    }

    @Test
    public void run() {
        methodTest2();
    }

}
