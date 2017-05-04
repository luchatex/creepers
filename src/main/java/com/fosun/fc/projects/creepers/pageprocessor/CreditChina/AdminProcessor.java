package com.fosun.fc.projects.creepers.pageprocessor.CreditChina;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.BaseTaskListProcessor;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.JsonValidator;
import com.virjar.dungproxy.client.util.PoolUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;
import us.codecraft.webmagic.utils.HttpConstant;

/***
 * 
 * <p>
 * 信用中国-行政许可/处罚全量主页面查询爬取
 * </p>
 * 
 * @author LiZhanPing 2016-8-22 00:49:33
 */
@Component
public class AdminProcessor extends BaseTaskListProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private Site site;

    public static String INDEX_URL = "http://www.creditchina.gov.cn/publicity_info_search?t=" + System.currentTimeMillis();

    private static final String JSON_KEY_RESULTS = "results";
    private static final String JSON_KEY_NAME = "name";

    @Override
    public void process(Page page) {
        CreepersParamDTO param = page.getResultItems().get(BaseConstant.PARAM_DTO_KEY);

        // 新增下一个链接任务
        Request request = creepersTaskListServiceImpl.popRequest(param.getSearchKeyWordForString());
        if (request != null && null != request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING)) {
            String stringNameValuePair = (String) request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING);
            HashMap<String,String> map = JSON.parseObject(stringNameValuePair, new TypeReference<HashMap<String,String>>() {});
            map.put("t", String.valueOf(System.currentTimeMillis())); 
            request = CommonMethodUtils.buildDefaultRequest(map,request);
            page.addTargetRequest(request);
        }
        logger.info("======================>>>  AdminProcessor start!");

        Json jsonJ = page.getJson();
        if (jsonJ != null) {
            logger.info("======================>>>  Json:" + jsonJ.toString());
            if (page.getJson().regex("HTTP/1.1 400 Bad Request").match()) {
                page.setSkip(true);
                logger.info("IP offline!");
                PoolUtil.offline(page.getResultItems().get(BaseConstant.HTTP_CLIENT_CONTEXT));
                logger.info("page content HTTP/1.1 400 Bad Request!!");
                logger.info("======================>>>AdminProcessor end!");
                return;
            }
        }

        try {
            if (JsonValidator.validate(page.getJson().toString(),false)) {
                JSONObject json;
                json = page.getJson().toObject(JSONObject.class);
                logger.debug("page.json:" + json.toJSONString());
                JSONArray jsonArr = json.getJSONArray(JSON_KEY_RESULTS);
                logger.debug("results:" + jsonArr.toJSONString());
                List<Map<String, String>> recordList = new ArrayList<Map<String, String>>();
                // 添加公司信用详情查看请求
                for (int i = 0; i < jsonArr.size(); i++) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    
                    //唯一标识号
                    String name = jsonArr.getJSONObject(i).getString(JSON_KEY_NAME);
                    map.put(CreepersConstant.TCreepersAdminColumn.KEY.getValue(), name);
                    logger.debug(CreepersConstant.TCreepersAdminColumn.KEY.getValue() + ":" + name);
                    
                    //信息类型
                    map.put(CreepersConstant.TCreepersAdminColumn.TYPE.getValue(), param.getSearchKeyWordForString());
                    logger.debug(CreepersConstant.TCreepersAdminColumn.TYPE.getValue() + ":" + param.getSearchKeyWordForString());
                    
                    //备注
                    map.put(CreepersConstant.TCreepersAdminColumn.MEMO.getValue(), jsonArr.getJSONObject(i).getString("encryStr"));
                    logger.debug(CreepersConstant.TCreepersAdminColumn.MEMO.getValue() + ":" + jsonArr.getJSONObject(i).getString("encryStr"));
                    
                    //信息内容
                    map.put(CreepersConstant.TCreepersAdminColumn.CONTENT.getValue(), jsonArr.getString(i));
                    logger.debug(CreepersConstant.TCreepersAdminColumn.CONTENT.getValue() + ":" + jsonArr.getString(i));
                    recordList.add(map);
                }
                page.putField(BaseConstant.RESULT_DATA_KEY, recordList);
                page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
                logger.info(page.getResultItems().get(BaseConstant.RESULT_DATA_KEY).toString());
            } else {
                page.setSkip(true);
                logger.debug(page.getJson().toString());
                logger.info("page.getJson() isn't a json or is blank");
            }
            logger.info("======================>>>  AdminProcessor   end!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("AdminProcessor process error:", e);
            String errorInfo = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
            param.setErrorInfo("AdminProcessor process error:" + errorInfo);
            param.setErrorPath(getClass().toString());
            creepersExceptionHandleServiceImpl.handleJobExceptionAndPrintLog(param);
            logger.error("======================>>>AdminProcessor end!");
        }
    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("www.creditchina.gov.cn").setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                    .setRetryTimes(5).setCycleRetryTimes(1).setSleepTime(7000)
                    .setTimeOut(120000).setCharset("UTF-8").addHeader("Referer", "http://www.creditchina.gov.cn/search_all")
                    .addHeader("Origin", "http://www.creditchina.gov.cn");
        }
        return site;
    }

    public static void main(String[] args) throws IOException {
        Request request = new Request("https://www.creditchina.gov.cn/publicity_info_search?page=67");
        request.setMethod(HttpConstant.Method.POST);
        NameValuePair[] nameValuePair = new NameValuePair[11];
        nameValuePair[0] = new BasicNameValuePair("keyword", "");
        nameValuePair[1] = new BasicNameValuePair("searchtype", "1");
        nameValuePair[2] = new BasicNameValuePair("objectType", "2");
        nameValuePair[3] = new BasicNameValuePair("areas", "");
        nameValuePair[4] = new BasicNameValuePair("creditType", "");
        nameValuePair[5] = new BasicNameValuePair("dataType", "1");
        nameValuePair[6] = new BasicNameValuePair("areaCode", "");
        nameValuePair[7] = new BasicNameValuePair("templateId", "");
        nameValuePair[8] = new BasicNameValuePair("exact", "0");
        nameValuePair[9] = new BasicNameValuePair("page", "67");
        nameValuePair[10] = new BasicNameValuePair("t", String.valueOf(System.currentTimeMillis()));
        request.putExtra(BaseConstant.POST_NAME_VALUE_PAIR, nameValuePair);
        Spider.create(new AdminProcessor()).setDownloader(new DungProxyDownloader()).thread(1).addRequest(request)
                .run();
    }
}
