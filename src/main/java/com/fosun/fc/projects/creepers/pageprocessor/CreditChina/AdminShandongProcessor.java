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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/***
 * <p>
 * 信用中国-行政处罚山东
 * </p>
 *
 * @author Luxin 2017-3-20 13:43:33
 */
@Component
public class AdminShandongProcessor extends BaseTaskListProcessor implements PageProcessor {

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
        int index = Integer.parseInt(page.getUrl().toString().split("page=")[1]);
        //sd
        List<Selectable> list = page.getHtml().xpath("/html/body/div/table[2]/tbody/tr").nodes();
        JSONArray jsonArray = new JSONArray();
        for (int i = 1; i < list.size(); i++) {
            JSONObject jo = new JSONObject();
            jo.put(COMPANYNAME, list.get(i).xpath("//td[1]/a/allText()").toString());
            jo.put(ANNCOUNCETYPE, list.get(i).xpath("saction").toString());
            jo.put(ANNOUNCEINSTITUTE, list.get(i).xpath("//td[2]/a/allText()").toString());
            jo.put(UPDATETIME, list.get(i).xpath("//td[4]/a/allText()").toString());
            jsonArray.add(jo);
        }
        try {
            FileWriter fileWriter = new FileWriter(new File("/data/sd/" + index + ".txt"));
            fileWriter.write(jsonArray.toString());
            fileWriter.close();
            System.out.println("写入index:" + index);
        } catch (IOException e) {
            System.out.println(e);
        }

        try {
            Random random = new Random();
            int result = random.nextInt(8);
            Thread.sleep(result * 1000);
        } catch (InterruptedException e) {
            System.out.println(e);
        }

    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("http://www.creditsd.gov.cn\").setUserAgent(\"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36\")\n" +
                    "                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(1000)\n" +
                    "                    .setTimeOut(60000).setCharset(\"GB23.zjcredit.gov.cn").setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(5000)
                    .setTimeOut(30000).setCharset("utf-8");
        }
        return site;
    }

    public static void main(String[] args) {
        try {
            Spider spider = new Spider(new AdminShandongProcessor());
            for (int i = 8470; i < 8471; i++) {
                spider.addUrl("http://www.creditsd.gov.cn/creditsearch.punishmentList.phtml?id=&keyword=&page=" + i);
            }
            spider.thread(20);
            spider.run();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
