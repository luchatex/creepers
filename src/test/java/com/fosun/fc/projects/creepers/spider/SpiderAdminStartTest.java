package com.fosun.fc.projects.creepers.spider;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONArray;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CreditChina.AdminProcessor;
import com.fosun.fc.projects.creepers.pipeline.CreditChina.AdminPiepline;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;

public class SpiderAdminStartTest extends SpiderBaseTest {

    @Autowired(required = true)
    private AdminProcessor adminProcessor2;

    @Autowired(required = true)
    private AdminPiepline adminPiepline;

    @Autowired(required = true)
    private DungProxyDownloader dungProxyDownloader;


    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    private static final String KEY_TOTAL_PAGE = "totalPage";
    private static final String KEY_PAGE_NO = "pageNo";
    private static final String KEY_THREAD_NUM = "threadNum";

    @Test
    public void run() {
        methodTest();
    }

    public void methodTest() {
        CreepersParamDTO param = new CreepersParamDTO();
        String url = "http://www.creditchina.gov.cn/publicity_info_search?" + "1";
        Map<String, String> map = new HashMap<String, String>();
        map.put("searchtype", "1");
        map.put("objectType", "2");
        map.put("dataType", "1");
        map.put("exact", "0");
        map.put("keyword", "");
        map.put("areas", "");
        map.put("creditType", "");
        map.put("areaCode", "");
        map.put("templateId", "");
        map.put("page", "1");
        map.put("t", String.valueOf(System.currentTimeMillis()));
        param.setNameValuePair(map);
        param.putSearchKeyWord(BaseConstant.TaskListType.ADMIN_SACTION_LIST.getValue());
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, "http://www.creditchina.gov.cn/publicity_info_search?");
        Request request = CommonMethodUtils.buildDefaultRequest(map, url);
        request.putExtra(KEY_TOTAL_PAGE, 3220016);
        request.putExtra(KEY_PAGE_NO, 1);
        request.putExtra(KEY_THREAD_NUM, 1);
        Spider.create(adminProcessor2).setDownloader(new DungProxyDownloader().setParam(param))
                .addPipeline(adminPiepline).thread(1).addRequest(request).run();
    }


    public void test() {
        int threadNum = 1;
        CreepersParamDTO param = new CreepersParamDTO();
        param.putSearchKeyWord(BaseConstant.TaskListType.ADMIN_LICENSE_LIST.getValue());
        param.setTaskType(BaseConstant.TaskListType.ADMIN_LICENSE_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());

        // 启动爬虫
        logger.info("=============>>启动爬虫!");
        Spider spider = Spider.create(adminProcessor2).setDownloader(dungProxyDownloader.setParam(param))
                .thread(threadNum).setExitWhenComplete(false);
        // 初始化Request
        for (int i = 0; i < threadNum; i++) {
            // 初始化Request
            Request request = creepersTaskListServiceImpl
                    .popRequest(BaseConstant.TaskListType.ADMIN_LICENSE_LIST.getValue());
            if (request != null) {
                param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, request.getUrl());
                if (StringUtils.isNoneBlank(request.getUrl()) && request.getUrl().contains("t=")) {
                    request.setUrl(request.getUrl().substring(0, request.getUrl().indexOf("t=")) + "t="
                            + new Date().getTime());
                }
                if (null != request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR)) {
                    JSONArray jsonArray = (JSONArray) request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR);
                    NameValuePair[] nameValuePairs = new NameValuePair[jsonArray.size()];
                    for (int j = 0; j < jsonArray.size(); j++) {
                        nameValuePairs[j] = jsonArray.getObject(j, NameValuePair.class);
                    }
                    request.putExtra(BaseConstant.POST_NAME_VALUE_PAIR, nameValuePairs);
                }
                spider.addRequest(request);
            }
        }
        spider.run();
        logger.info("=============>>CreepersAdminServiceImpl.processByRequest en1d!");
    }

}
