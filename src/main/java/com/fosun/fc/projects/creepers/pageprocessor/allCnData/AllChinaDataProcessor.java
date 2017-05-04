package com.fosun.fc.projects.creepers.pageprocessor.allCnData;

import java.sql.SQLException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.BaseMedicalProcessor;
import com.fosun.fc.projects.creepers.pipeline.allCnData.AllCnDataJsonFilePipline;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.JdbcUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 
 * <p>
 * Demo: 数据中华企业名单
 * </p>
 * 
 * @author MaXin
 * @since 2017-3-16 02:19:23
 * @see
 */
@Component("allChinaDataProcessor")
public class AllChinaDataProcessor extends BaseMedicalProcessor implements PageProcessor {

    public static final String INDEX_URL = "http://data.allchinadata.com/Enterprise/SearchEnterprise.ashx?page=1";
    public static final String URL_PAGE_TEMPLATE = "http://app1.sfda.gov.cn/datasearch/face3/search.jsp?tableId=24&bcId=118715593187347941914723540896&curstart=";

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private Site site;

    public AllChinaDataProcessor() {
        if (null == site) {
            site = Site.me().setDomain("data.allchinadata.com").setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36")
                    .setRetryTimes(5).setCycleRetryTimes(1)
                    .setSleepTime(CommonMethodUtils.randomSleepTime())
                    .addHeader("Referer", "http://data.allchinadata.com/Enterprise/Result.aspx?Keyword=&KeywordType=KeywordAny")
                    .addHeader("Origin", "http://data.allchinadata.com")
                    .addHeader("Accept", "application/json, text/javascript, */*; q=0.01")
                    .setCharset("UTF-8")
                    .setTimeOut(180000)
                    .setUseGzip(true)
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    ;
        }

    }

    @Override
    public void process(Page page) {
        // 新增下一个链接任务
        Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.ALL_CHINA_DATA.getValue());
        if (request != null && null != request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING)) {
            String stringNameValuePair = (String) request.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING);
            HashMap<String,String> map = JSON.parseObject(stringNameValuePair, new TypeReference<HashMap<String,String>>() {});
            request = CommonMethodUtils.buildDefaultRequest(map,request);
            page.addTargetRequest(request);
            logger.info("======>>> add request succeed!");
        }
        logger.info("========================>>AllChinaDataProcessor:  start");
        ResultItems resultItem = page.getResultItems();
        JSONObject json;
        json = page.getJson().toObject(JSONObject.class);
        logger.debug("page.json:" + json.toJSONString());
        JSONArray jsonArr = json.getJSONArray("rows");
        logger.info("jsonArray Size:"+jsonArr.size());
        logger.debug("results:" + jsonArr.toJSONString());
        if (jsonArr.size() == 0) {
            logger.info("查询结果为空!  url:" + page.getUrl().toString());
            page.setSkip(true);
            return;
        }
        page.putField(BaseConstant.RESULT_DATA_KEY, jsonArr.toString());
        page.putField(BaseConstant.JSON_FILE_NAME, page.getUrl().regex("=(.*)").toString());
        logger.info(resultItem.get(BaseConstant.RESULT_DATA_KEY).toString());
        logger.info("==============>>creepersTaskListServiceImpl.updateFlagByUrl to COMPLETED: start");
        String updateQuery = "update t_creepers_task_list t set t.flag = '1' where t.url = '" + page.getRequest().getUrl() + "'";
        try {
            JdbcUtil.execute(updateQuery);
        } catch (ClassNotFoundException e) {
            logger.error("JdbcUtil.execute  ClassNotFoundException!");
            e.printStackTrace();
        } catch (SQLException e) {
            logger.error("JdbcUtil.execute  SQLException!");
            e.printStackTrace();
        }
        logger.info("==============>>creepersTaskListServiceImpl.updateFlagByUrl to COMPLETED: end");
        logger.info("========================>>AllChinaDataProcessor:  end");
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String threadNum = "1";
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.ALL_CHINA_DATA.getValue());
        param.putNameValuePair("page", "1");
        param.putNameValuePair("rows", "390");
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
        System.out.println("param json："+JSON.toJSONString(param.getNameValuePair()));
        Request request = CommonMethodUtils.buildDefaultRequest(param, INDEX_URL);
        Spider.create(new AllChinaDataProcessor()).setDownloader(new DungProxyDownloader().setParam(param))
                .addPipeline(new AllCnDataJsonFilePipline()).addRequest(request).thread(Integer.valueOf(threadNum)).run();
    }
}
