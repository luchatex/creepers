package com.fosun.fc.projects.creepers.pageprocessor.CreditChina;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.BaseTaskListProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * <p>
 * 信用中国-行政处罚京津冀
 * </p>
 *
 * @author Luxin 2017-3-20 13:43:33
 */
@Component
public class AdminJJJProcessor extends BaseTaskListProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    //公司名
    private final static String COMPANYNAME = "companyName";
    //处罚类型
    private final static String ANNCOUNCETYPE = "announceType";
    //委办局
    private final static String ANNOUNCEINSTITUTE = "announceInstitute";
    //发布时间
    private final static String UPDATETIME = "updateTime";

    private Site site;

    @Override
    public void process(Page page) {

        CreepersParamDTO paramDTO = page.getResultItems().get(BaseConstant.PARAM_DTO_KEY);
        Request request = creepersTaskListServiceImpl.popRequest(paramDTO.getTaskType());
        if (request != null){
            page.addTargetRequest(request);
        }

        //jjj
        List<HashMap<String, String>> recordList = new ArrayList<HashMap<String, String>>();
        List<Selectable> divList =  page.getHtml().xpath("//*[@class='list']/").nodes();

        //遍历每页的记录
//        for (int i = 1; i < divList.size(); i++) {
//            HashMap<String, String> recordMap = new HashMap<String, String>();
//            recordMap.put(COMPANYNAME, divList.get(i).xpath("/tbody/tr/td[1]/a/allText()").toString());
//            recordMap.put(ANNCOUNCETYPE, divList.get(i).xpath("/tbody/tr/td[2]/allText()").toString());
//            recordMap.put(ANNOUNCEINSTITUTE, divList.get(i).xpath("/tbody/tr/td[2]/allText()").toString());
//            recordMap.put(UPDATETIME, divList.get(i).xpath("/tbody/tr/td[4]/allText()").toString());
//            recordList.add(recordMap);
//        }
//        page.putField(BaseConstant.RESULT_DATA_KEY, recordList);
//        page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
        String args[] = page.getUrl().toString().split("&page=");
        JSONArray jsonArray = new JSONArray();
        for (int i=1;i<divList.size();i++){
            JSONObject jo = new JSONObject();
            jo.put(COMPANYNAME, divList.get(i).xpath("/tbody/tr/td[1]/a/allText()").toString());
            jo.put(ANNCOUNCETYPE, divList.get(i).xpath("/tbody/tr/td[2]/allText()").toString());
            jo.put(ANNOUNCEINSTITUTE, divList.get(i).xpath("/tbody/tr/td[2]/allText()").toString());
            jo.put(UPDATETIME, divList.get(i).xpath("/tbody/tr/td[4]/allText()").toString());
            jsonArray.add(jo);
        }
        page.putField("result", jsonArray.toString());
        page.putField("pageNum", args[1]);
        page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("http://cxcj.creditbj.gov.cn\").setUserAgent(\"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36\")\n" +
                    "                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(7000)\n" +
                    "                    .setTimeOut(60000).setCharset(\"GB23.zjcredit.gov.cn").setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(7000)
                    .setTimeOut(30000).setCharset("utf-8");
        }
        return site;
    }

    public static void main(String[] args) {
        try {
            Spider.create(new AdminJJJProcessor())
                    .addUrl("http://cxcj.creditbj.gov.cn/newxybj/punishList.jspx?q=&channelId=135&type=punish&region=%E5%85%A8%E9%83%A8&typeName=%E4%B8%BB%E4%BD%93%E5%90%8D%E7%A7%B0&page=2")
                    .thread(1)
                    .run();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
