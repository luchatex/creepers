package com.fosun.fc.projects.creepers.spider;

import java.util.HashMap;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.allCnData.AllChinaDataProcessor;
import com.fosun.fc.projects.creepers.pipeline.allCnData.AllCnDataJsonFilePipline;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class SpiderAllCnDataStartTest extends SpiderBaseTest {

    @Autowired
    private AllChinaDataProcessor allChinaDataProcessor;


    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    // 数据中华
    @SuppressWarnings("unused")
    public void methodTest1() {
        
        String INDEX_URL = "http://data.allchinadata.com/Enterprise/SearchEnterprise.ashx?page=";
        
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.setTaskType(BaseConstant.TaskListType.ALL_CHINA_DATA.getValue());
        param.putSearchKeyWord(BaseConstant.TaskListType.ALL_CHINA_DATA.getValue());

        int threadNum = 1;
        Spider spider = Spider.create(allChinaDataProcessor).addPipeline(new AllCnDataJsonFilePipline())
                .setDownloader(new DungProxyDownloader().setParam(param));
        Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.ALL_CHINA_DATA.getValue());
        if (request != null && null != request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING)) {
            String stringNameValuePair = (String) request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING);
            HashMap<String, String> map = JSON.parseObject(stringNameValuePair, new TypeReference<HashMap<String, String>>() {});
            request = CommonMethodUtils.buildDefaultRequest(map, request);
            spider.addRequest(request);
        }
        
         /*param.putNameValuePair("page", "5");
         param.putNameValuePair("rows", "195");
         param.putNameValuePair("DatabaseType", "Year");
         param.putNameValuePair("KeywordType", "KeywordAny");
         param.putNameValuePair("OrderByColumn", "SaleVolume-true");
         param.putNameValuePair("Keyword", "");
         param.putNameValuePair("TimeString", "");
         param.putNameValuePair("Region", "");
         param.putNameValuePair("Industry", "");
         param.putNameValuePair("IndustryType", "");
         param.putNameValuePair("RegistrationType", "");
         param.putNameValuePair("SaleVolumeStart", "");
         param.putNameValuePair("SaleVolumeEnd", "");
         param.putNameValuePair("AssetSizeStart", "");
         param.putNameValuePair("AssetSizeEnd", "");
         param.putNameValuePair("StaffNumberStart", "");
         param.putNameValuePair("StaffNumberEnd", "");
         param.putNameValuePair("TotalProfitStart", "");
         param.putNameValuePair("TotalProfitEnd", "");
         param.putNameValuePair("OwnerEquitiesStart", "");
         param.putNameValuePair("OwnerEquitiesEnd", "");
         param.putNameValuePair("ROEStart", "");
         param.putNameValuePair("ROEEnd", "");
         param.putNameValuePair("OpeningYearStart", "");
         param.putNameValuePair("OpeningYearEnd", "");
         Request request = CommonMethodUtils.buildDefaultRequest(param,INDEX_URL+"3-1");
         param.putNameValuePair("page", "6");
         Request request2 = CommonMethodUtils.buildDefaultRequest(param,INDEX_URL+"3-2");
         param.putNameValuePair("page", "7");
         Request request3 = CommonMethodUtils.buildDefaultRequest(param,INDEX_URL+"4-1");
         param.putNameValuePair("page", "8");
         Request request4 = CommonMethodUtils.buildDefaultRequest(param,INDEX_URL+"4-2");
         spider.addRequest(request);
         spider.addRequest(request2);
         spider.addRequest(request3);
         spider.addRequest(request4);*/
         spider.thread(threadNum).run();
    }

    @Test
    public void run() {
        methodTest1();
    }

}
