package com.fosun.fc.projects.creepers.spider;

import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dao.CreepersCourtDishonestDao;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.downloader.HttpRequestDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CreditChina.SupremeCourtDishonestyBlackListProcessor;
import com.fosun.fc.projects.creepers.pipeline.CreditChina.SupremeCourtDishonestyBlackListPipeline;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

public class SpiderCourtDishonestyStartTest extends SpiderBaseTest {

    @Autowired(required=true)
    private SupremeCourtDishonestyBlackListProcessor supremeCourtDishonestyBlackListProcessor;
    
    @Autowired(required=true)
    private SupremeCourtDishonestyBlackListPipeline supremeCourtDishonestyBlackListPipeline;
    
    @Autowired
    private CreepersCourtDishonestDao courtDishonestDao;
    
    public void methodTest() {
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.COURT_DISHONESTY_LIST.getValue());
        String url = "http://www.creditchina.gov.cn/shiXinRen?t=" + new Date().getTime() + "&currentPageNo=1";
        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        NameValuePair[] nameValuePair = new NameValuePair[2];
        nameValuePair[0] = new BasicNameValuePair("keyword", "");
        nameValuePair[1] = new BasicNameValuePair("page", "1");
        request.putExtra(BaseConstant.POST_NAME_VALUE_PAIR, nameValuePair);
        Spider.create(supremeCourtDishonestyBlackListProcessor).addPipeline(supremeCourtDishonestyBlackListPipeline)
                .setDownloader(new DungProxyDownloader().setParam(param)).thread(1).addRequest(request).run();
    }

    public void daoTest(){
//        TCreepersCourtDishonest entity = new TCreepersCourtDishonest();
//        entity.setName("吉林成城集团股份有限公司");
//        CommonMethodUtils.setByDT(entity);
//        courtDishonestDao.save(entity);
        courtDishonestDao.findTop1ByNameOrderByCreatedDtAsc("吉林成城集团股份有限公司");
    }
    
    @Test
    public void run() {
//        methodTest();
        daoTest();
    }

}
