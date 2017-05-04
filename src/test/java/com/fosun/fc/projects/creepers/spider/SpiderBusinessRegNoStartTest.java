package com.fosun.fc.projects.creepers.spider;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.CreditChinaDungDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CreditChina.BusinessRegNoProcessor;
import com.fosun.fc.projects.creepers.pipeline.CreditChina.BusinessRegNoJsonFilePipline;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class SpiderBusinessRegNoStartTest extends SpiderBaseTest {

    @Autowired
    private BusinessRegNoProcessor businessRegNoProcessor;

    @SuppressWarnings("unused")
    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    // 信用中国工商注册号查询
    public void methodTest1() {
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.setTaskType(BaseConstant.TaskListType.BUSINESS_REG_NO.getValue());
        param.putSearchKeyWord(BaseConstant.TaskListType.BUSINESS_REG_NO.getValue());

        int threadNum = 1;
        Spider spider = Spider.create(businessRegNoProcessor).addPipeline(new BusinessRegNoJsonFilePipline())
                .setDownloader(new CreditChinaDungDownloader().setParam(param));
//        for (int i = 0; i < threadNum; i++) {
//            Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.BUSINESS_REG_NO.getValue());
//            if (request != null && null != request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING)) {
//                String stringNameValuePair = (String) request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING);
//                HashMap<String, String> map = JSON.parseObject(stringNameValuePair, new TypeReference<HashMap<String, String>>() {});
//                map.put("t", String.valueOf(System.currentTimeMillis()));
//                request = CommonMethodUtils.buildDefaultRequest(map, request);
//                spider.addRequest(request);
//            }
//        }

        param.putNameValuePair("keyword", "310115400176517");
        param.putNameValuePair("searchtype", "3");
        param.putNameValuePair("objectType", "2");
        param.putNameValuePair("areas", "");
        param.putNameValuePair("creditType", "");
        param.putNameValuePair("dataType", "1");
        param.putNameValuePair("areaCode", "");
        param.putNameValuePair("templateId", "");
        param.putNameValuePair("exact", "0");
        param.putNameValuePair("page", "1");
        long time = System.currentTimeMillis();
        param.putNameValuePair("t", String.valueOf(time));
        Request request = CommonMethodUtils.buildDefaultRequest(param.getNameValuePair(), "http://www.creditchina.gov.cn/credit_code_info_search?at2da=310115400176517");
        spider.addRequest(request);
        spider.thread(threadNum).run();
    }

    @Test
    public void run() {
        methodTest1();
    }

}
