package com.fosun.fc.projects.creepers.spider;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.MedicalInstrumentDomesticProcessor;
import com.fosun.fc.projects.creepers.pipeline.CFDA.MedicalPipline;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class SpiderMedicalInstrumentStartTest extends SpiderBaseTest {

    @Autowired(required = true)
    private MedicalInstrumentDomesticProcessor medicalInstrumentDomesticProcessor;

    @Autowired(required = true)
    private MedicalPipline medicalPipline;
    
    @Autowired
    private DungProxyDownloader dungProxyDownloader;

    // 国产器械
    public void methodTest1() {
        String threadNum = "1";
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_DOMESTIC_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, MedicalInstrumentDomesticProcessor.INDEX_URL);
        Request request = CommonMethodUtils.buildDefaultRequest(param, "http://qy1.sfda.gov.cn/datasearch/face3/content.jsp?tableId=26&tableName=TABLE26&tableView=国产器械&Id=5450");
        request.putExtra(BaseConstant.PARAM_EXTRA_CURRENT_DETAIL_NO, "7961");
        request.putExtra(BaseConstant.PARAM_EXTRA_THREAD_NUM, threadNum);

        Spider.create(medicalInstrumentDomesticProcessor).addPipeline(medicalPipline)
                .setDownloader(dungProxyDownloader.setParam(param))
                .addRequest(request)
                .thread(Integer.valueOf(threadNum)).run();
    }


    @Test
    public void run() {
        methodTest1();
    }

}
