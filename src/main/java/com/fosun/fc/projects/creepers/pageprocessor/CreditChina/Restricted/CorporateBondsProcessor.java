package com.fosun.fc.projects.creepers.pageprocessor.CreditChina.Restricted;

import com.fosun.fc.projects.creepers.utils.JdbcUtil;
import com.fosun.fc.projects.creepers.utils.StringUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by luxin on 4/18/17.
 */
public class CorporateBondsProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private Site site;
    JsonParser parser = new JsonParser();

    @Override
    public void process(Page page) {
        String index = page.getUrl().toString().split("creditinfo")[1].split("_")[0];
        try {
            JsonArray jsonArray = (JsonArray) parser.parse(page.getRawText());
            int size = jsonArray.size();
            List<String> sqlList = new ArrayList<String>();
            for (int i = 0; i < size; i++) {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                if (StringUtils.isEmpty(object.get("INAME").getAsString())||StringUtils.isEmpty(object.get("CARDNUMBER").getAsString())||StringUtils.isEmpty(object.get("AREA_NAME").getAsString())){
                    continue;
                }
                String sql = "insert into t_creepers_punish_list(id,type,name,code,province,memo,flag,version,created_by,created_dt,updated_by,updated_dt) "
                        + "values(SEQ_CREEPERS_PUNISH_LIST.nextval"
                        + ",'0'"
                        + ",'" + object.get("INAME").getAsString().replace("'","") + "'"
                        + ",'" + object.get("CARDNUMBER").getAsString().replace("'","") + "'"
                        + ",'" + object.get("AREA_NAME").getAsString().replace("'","") + "'"
                        + ",'" + object.toString() + "'"
                        + ",'0'"
                        + ",'0'"
                        + ",'admin',sysdate"
                        + ",'admin',sysdate)";
                sqlList.add(sql);
            }
            JdbcUtil.execute(sqlList);
            sqlList.clear();
            System.out.println(index + "写入成功!");
        } catch (Exception e) {
            System.out.println("index: "+index+" Write DB ERROR!");
        }
    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("http://cxcj.creditbj.gov.cn").setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(5000)
                    .setTimeOut(60000).setCharset("UTF-8");
        }
        return site;
    }

    public static void main(String args[]) {
        int threadNum = 10;
        Spider spider = new Spider(new CorporateBondsProcessor());
        for (int i = 1; i <= 1227; i++) {
            Long d = System.currentTimeMillis();
            spider.addUrl("http://www.creditchina.gov.cn/uploads/creditinfo/" + i + "?_=" + d);
        }
        spider.thread(threadNum);
        spider.run();
    }
}
