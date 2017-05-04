package com.fosun.fc.projects.creepers.pageprocessor.CreditChina;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.BaseCreepersListProcessor;
import com.fosun.fc.projects.creepers.service.ICreepersTaskListService;
import com.fosun.fc.projects.creepers.utils.JsonValidator;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

@Component
public class CreditBlackListProcessor extends BaseCreepersListProcessor implements PageProcessor {

    private Site site;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ICreepersTaskListService creepersTaskListServiceImpl;

    private static final String BASE_URL = "http://www.creditchina.gov.cn";

    private static final String JSON_KEY_INAME = "INAME";
    private static final String JSON_KEY_CARDNUMBER = "CARDNUMBER";
    private static final String JSON_KEY_AREA_NAME = "AREA_NAME";

    @Override
    public void process(Page page) {

        CreepersParamDTO param = page.getResultItems().get(BaseConstant.PARAM_DTO_KEY);
        Request request = creepersTaskListServiceImpl.popRequest(param.getTaskType());
        if (request != null) {
            page.addTargetRequest(request);
        }
        logger.info("======================>>>  CorporateBondsBlacklistProcessor.Process start!");
        try {
            if (JsonValidator.validate(page.getJson().toString())) {
                List<HashMap<String, String>> tCreepersCourtCorpBonds = new ArrayList<HashMap<String, String>>();
                Map<String, HashMap<String, String>> filterMap = new HashMap<String, HashMap<String, String>>();
                String jsonStr = page.getJson().toString();
                logger.info("JSONArray:" + jsonStr);
                JSONArray jsonArr = JSON.parseArray(jsonStr);
                for (int i = 0; i < jsonArr.size(); i++) {
                    JSONObject jsonObj = jsonArr.getJSONObject(i);
                    String name = jsonObj.getString(JSON_KEY_INAME);
                    String code = jsonObj.getString(JSON_KEY_CARDNUMBER);
                    String province = jsonObj.getString(JSON_KEY_AREA_NAME);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map.put(CreepersConstant.TCreepersCreditBlackListColumn.TYPE.getValue(), param.getTaskType());
                    map.put(CreepersConstant.TCreepersCreditBlackListColumn.NAME.getValue(), name);
                    map.put(CreepersConstant.TCreepersCreditBlackListColumn.CODE.getValue(), code);
                    map.put(CreepersConstant.TCreepersCreditBlackListColumn.PROVINCE.getValue(), province);
                    filterMap.put(name, map);
                }
                for(Entry<String, HashMap<String, String>> entry:filterMap.entrySet()){
                    tCreepersCourtCorpBonds.add(entry.getValue());
                }
                page.putField(CreepersConstant.TableNamesCreditChina.T_CREEPERS_CREDIT_BLACK_LIST.getMapKey(),
                        tCreepersCourtCorpBonds);
                page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
            } else {
                page.setSkip(true);
                logger.debug(page.getJson().toString());
                logger.info("page.getJson() isn't a json or is blank");
            }
            logger.info("======================>>>  CreditBlackListProcessor end!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("CreditBlackListProcessor error:", e);
            String errorInfo = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
            param.setErrorInfo("CreditBlackListProcessor error:" + errorInfo);
            param.setErrorPath(getClass().toString());
            creepersExceptionHandleServiceImpl.handleJobExceptionAndPrintLog(param);
            logger.error("======================>>>  CreditBlackListProcessor end!");
        }
    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain(BASE_URL)
                    .setUserAgent(
                            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(7000).setTimeOut(60000).setCharset("UTF-8").addHeader("Content-Length", "50000");
        }
        return site;
    }

    public static void main(String args[]) {
        Spider.create(new CreditBlackListProcessor()).setDownloader(new DungProxyDownloader())
                .addUrl("http://www.creditchina.gov.cn/uploads/creditinfo/247").thread(1).run();
    }

}
