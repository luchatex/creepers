package com.fosun.fc.projects.creepers.pageprocessor.CreditChina;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.HttpRequestDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.BaseTaskListProcessor;
import com.fosun.fc.projects.creepers.pipeline.CreditChina.BusinessRegNoJsonFilePipline;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.JsonValidator;
import com.virjar.dungproxy.client.util.PoolUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

/***
 * 
 * <p>
 * 
 * 信用中国-工商注册号查询
 * 
 * </p>
 * 
 * @author MaXin
 * @see 2017-3-22 02:30:16
 */
@Component("businessRegNoProcessor")
public class BusinessRegNoProcessor extends BaseTaskListProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private Site site;

    public static String INDEX_URL = "http://www.creditchina.gov.cn/credit_code_info_search?t=" + System.currentTimeMillis();

    private static final String JSON_KEY_RESULT = "result";
    private static final String JSON_KEY_RESULTS = "results";
    private static final String JSON_KEY_TOTAL_COUNT = "totalCount";

    @Override
    public void process(Page page) {
        CreepersParamDTO param = page.getResultItems().get(BaseConstant.PARAM_DTO_KEY);

        // 新增下一个链接任务
        Request request = creepersTaskListServiceImpl.popRequest(param.getSearchKeyWordForString());
        if (request != null && null != request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING)) {
            String stringNameValuePair = (String) request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING);
            HashMap<String, String> map = JSON.parseObject(stringNameValuePair, new TypeReference<HashMap<String, String>>() {
            });
            map.put("t", String.valueOf(System.currentTimeMillis()));
            request = CommonMethodUtils.buildDefaultRequest(map, request);
            page.addTargetRequest(request);
        }
        logger.info("======================>>>  BusinessRegNoProcessor start!");

        Json jsonJ = page.getJson();
        if (jsonJ != null) {
            logger.info("======================>>>  Json:" + jsonJ.toString());
            if (page.getJson().regex("HTTP/1.1 400 Bad Request").match()) {
                page.setSkip(true);
                logger.info("IP offline!");
                PoolUtil.offline(page.getResultItems().get(BaseConstant.HTTP_CLIENT_CONTEXT));
                logger.info("page content HTTP/1.1 400 Bad Request!!");
                logger.info("======================>>>BusinessRegNoProcessor end!");
                return;
            }
        }

        try {
            if (JsonValidator.validate(page.getJson().toString(), false)) {
                JSONObject json;
                json = page.getJson().toObject(JSONObject.class);
                logger.debug("page.json:" + json.toJSONString());
                JSONObject jsonObj = json.getJSONObject(JSON_KEY_RESULT);
                String totalcount = jsonObj.getString(JSON_KEY_TOTAL_COUNT);
                if ("0".equals(totalcount)) {
                    page.setSkip(true);
                    creepersTaskListServiceImpl.updateFlagByUrl(page.getRequest().getUrl(), BaseConstant.TaskListFlag.NO_DATA_COMPLETED.getValue());
                    return;
                }
                JSONArray jsonArr = jsonObj.getJSONArray(JSON_KEY_RESULTS);
                logger.debug("results:" + jsonArr.toJSONString());
                page.putField(BaseConstant.RESULT_DATA_KEY, jsonArr.toJSONString());
                page.putField(BaseConstant.JSON_FILE_NAME, page.getUrl().regex("=(.*)").toString());
                logger.info("processor results:{}", page.getResultItems().get(BaseConstant.RESULT_DATA_KEY).toString());
            } else {
                page.setSkip(true);
                logger.debug(page.getJson().toString());
                logger.info("page.getJson() isn't a json or is blank");
            }
            logger.info("======================>>>  BusinessRegNoProcessor   end!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("BusinessRegNoProcessor process error:", e);
            String errorInfo = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
            param.setErrorInfo("BusinessRegNoProcessor process error:" + errorInfo);
            param.setErrorPath(getClass().toString());
            creepersExceptionHandleServiceImpl.handleJobExceptionAndPrintLog(param);
            logger.error("======================>>>BusinessRegNoProcessor end!");
        }
    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("www.creditchina.gov.cn").setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                    .setRetryTimes(5).setCycleRetryTimes(1).setSleepTime(0)
                    .setTimeOut(120000).setCharset("UTF-8").addHeader("Referer", "http://www.creditchina.gov.cn/search_all")
                    .addHeader("Origin", "http://www.creditchina.gov.cn");
        }
        return site;
    }

    public static void main(String[] args) throws IOException {
        CreepersParamDTO param = new CreepersParamDTO();
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
        Request request = CommonMethodUtils.buildDefaultRequest(param.getNameValuePair(), "http://www.creditchina.gov.cn/credit_code_info_search?atda=310115400176517");
        Spider.create(new BusinessRegNoProcessor()).setDownloader(new HttpRequestDownloader().setParam(param)).addPipeline(new BusinessRegNoJsonFilePipline()).thread(1).addRequest(request).run();
    }
}
