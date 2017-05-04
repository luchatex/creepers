package com.fosun.fc.projects.creepers.pageprocessor.CFDA;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.JdbcUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 
 * <p>
 * Demo: 国外医疗器械
 * </p>
 * 
 * @author MaXin
 * @since 2016-11-22 09:39:19
 * @see
 */
@Component("medicalInstrumentForeignProcessor")
public class MedicalInstrumentForeignProcessor extends BaseMedicalProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String INDEX_URL = "http://qy1.sfda.gov.cn/datasearch/face3/search.jsp?tableId=27&bcId=118103063506935484150101953610";

    private Site site;

    public MedicalInstrumentForeignProcessor() {
        if (null == site) {
            site = Site.me().setDomain("qy1.sfda.gov.cn").setUserAgent(CommonMethodUtils.getRandomUserAgent())
                    .setRetryTimes(5).setCycleRetryTimes(1)
                    .setSleepTime(CommonMethodUtils.randomSleepTime()).setTimeOut(90000);
        }

    }

    @Override
    public void process(Page page) {
        // 新增下一个链接任务
        Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue());
        if (request != null) {
            page.addTargetRequest(request);
        }
        logger.warn("========================>>MedicalInstrumentForeignProcessor:  start");
        ResultItems resultItem = page.getResultItems();
        CreepersParamDTO param = resultItem.get(BaseConstant.PARAM_DTO_KEY);

        if (page.getHtml().regex("没有相关信息").match()) {
            logger.info("没有相关记录!  url:" + page.getUrl().toString());
            page.setSkip(true);
            logger.info("========================>>MedicalInstrumentForeignProcessor:  end");
            String updateSQL = "update t_creepers_task_list t set t.flag = '4' where t.url = '" + page.getRequest().getUrl() + "'";
            try {
                JdbcUtil.execute(updateSQL);
            } catch (ClassNotFoundException e) {
                logger.error("JdbcUtil.execute  ClassNotFoundException!");
                e.printStackTrace();
            } catch (SQLException e) {
                logger.error("JdbcUtil.execute  SQLException!");
                e.printStackTrace();
            }
            return;
        }
        
        Map<String, String> result = new HashMap<String, String>();
        result.put(CreepersConstant.TCreepersMedicalColumn.TYPE.getValue(), BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue());
        List<Selectable> tdList = page.getHtml().xpath("//td[@bgcolor]//allText()").nodes();
        String key = StringUtils.EMPTY;
        String value = StringUtils.EMPTY;
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < tdList.size(); i++) {
            Selectable eachTd = tdList.get(i);
            String content = eachTd.toString();
            logger.debug("td:" + content);
            if (i % 2 == 0) {
                key = content;
            } else if (i % 2 == 1) {
                value = content;
                map.put(key, value);
                if ("注册证编号".equals(key)) {
                    result.put(CreepersConstant.TCreepersMedicalColumn.KEY.getValue(), value);
                    param.putSearchKeyWord(value);
                }
                key = StringUtils.EMPTY;
                value = StringUtils.EMPTY;
            }
        }
        list.add(map);
        result.put(CreepersConstant.TCreepersMedicalColumn.CONTENT.getValue(), JSONObject.toJSONString(list, BaseConstant.features));
        List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
        resultList.add(result);
        page.putField(BaseConstant.RESULT_DATA_KEY, resultList);
        page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
        logger.warn(resultItem.get(BaseConstant.RESULT_DATA_KEY).toString());
        logger.warn("========================>>MedicalInstrumentForeignProcessor:  end");
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String threadNum = "5";
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.MEDICAL_INSTRUMENT_FOREIGN_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        param.putOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL, INDEX_URL);
        Request request = CommonMethodUtils.buildDefaultRequest(param, param.getOrderUrl(BaseConstant.OrderUrlKey.INDEX_URL));
        request.putExtra(BaseConstant.PARAM_EXTRA_CURRENT_PAGE_NO, "1");
        request.putExtra(BaseConstant.PARAM_EXTRA_TOTAL_PAGE_NO, "100");
        request.putExtra(BaseConstant.PARAM_EXTRA_THREAD_NUM, threadNum);

        Spider.create(new MedicalInstrumentForeignProcessor()).setDownloader(new DungProxyDownloader().setParam(param))
                .addRequest(request).thread(Integer.valueOf(threadNum)).runAsync();
    }
}
