package com.fosun.fc.projects.creepers.pageprocessor.baidu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.downloader.HttpRequestDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.BaseCreepersListProcessor;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.JsonValidator;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/***
 * 
 * <p>
 * 信用中国主页面查询爬取
 * </p>
 * 
 * @author LiZhanPing 2016-8-22 00:49:33
 */
@Component
public class DishonestBaiduProcessor extends BaseCreepersListProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;
    
    private Site site;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0";

    private static final String JSON_KEY_DATA = "data";
    private static final String JSON_KEY_RESULT = "result";
    private static final String JSON_KEY_INAME = "iname";
    private static final String JSON_KEY_CARD_NUM = "cardNum";
    private static final String JSON_KEY_CASE_CODE = "caseCode";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CERT_NO = "certNo";
    private static final String COLUMN_CASE_CODE = "caseCode";
    private static final String COLUMN_CONTENT = "content";

    @Override
    public void process(Page page) {
        CreepersParamDTO param = page.getResultItems().get(BaseConstant.PARAM_DTO_KEY);
        // 新增下一个链接任务
        Request request = creepersTaskListServiceImpl.popRequest(param.getTaskType());
        if (request != null) {
            page.addTargetRequest(request);
        }
        logger.info("======================>>>  DishonestBaiduProcessor start!");
        try {
            String jsonStr = page.getJson().toString();
            if (!JsonValidator.validate(jsonStr)) {
                page.setSkip(true);
                logger.debug(page.getJson().toString());
                logger.info("page.getJson() isn't a json or is blank");
                return;
            }
            List<HashMap<String, Object>> results = new ArrayList<HashMap<String,Object>>();
            JSONObject json = JSONObject.parseObject(jsonStr);
            JSONArray dataArr = json.getJSONArray(JSON_KEY_DATA);
            if (dataArr.size()>0) {
                JSONArray jsonArr =dataArr.getJSONObject(0).getJSONArray(JSON_KEY_RESULT);
                for (int i = 0; i < jsonArr.size(); i++) {
                    HashMap<String, Object> map = new HashMap<String,Object>();
                    map.put(COLUMN_NAME, jsonArr.getJSONObject(i).get(JSON_KEY_INAME));
                    map.put(COLUMN_CERT_NO, jsonArr.getJSONObject(i).get(JSON_KEY_CARD_NUM));
                    map.put(COLUMN_CASE_CODE, jsonArr.getJSONObject(i).get(JSON_KEY_CASE_CODE));
                    map.put(COLUMN_CONTENT, jsonArr.getJSONObject(i).toJSONString());
                    results.add(map);
                }
            }
            page.putField(CreepersConstant.TableNamesOthers.T_CREEPERS_DISHONEST_BAIDU.getMapKey(), results);
            page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
            logger.info("======================>>>  DishonestBaiduProcessor end!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("DishonestBaiduProcessor process error:", e);
            String errorInfo = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
            param.setErrorInfo("DishonestBaiduProcessor process error:" + errorInfo);
            param.setErrorPath(getClass().toString());
            creepersExceptionHandleServiceImpl.handleExceptionAndPrintLog(param);
            logger.error("======================>>>  DishonestBaiduProcessor end!");
        }
    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("sp0.baidu.com").setUserAgent(USER_AGENT).setTimeOut(60000).setRetryTimes(3)
                    .setCycleRetryTimes(3).setSleepTime(9988);
        }
        return site;
    }

    public static void main(String[] args) throws IOException {
        String url = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?resource_id=6899&query=%E5%A4%B1%E4%BF%A1%E8%A2%AB%E6%89%A7%E8%A1%8C%E4%BA%BA%E5%90%8D%E5%8D%95&areaName=&ie=utf-8&oe=utf-8&format=json&cardNum=230811197703152113&iname=杨伟";
        Spider.create(new DishonestBaiduProcessor()).setDownloader(new HttpRequestDownloader()).thread(1).addUrl(url)
                .run();
    }
}
