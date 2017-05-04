package com.fosun.fc.projects.creepers.pageprocessor.CreditChina;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/***
 * <p>
 * 信用中国-行政处罚浙江
 * </p>
 *
 * @author Luxin 2017-2-27 9:57:33
 */
@Component
public class AdminZhejiangProcessor extends BaseTaskListProcessor implements PageProcessor {

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
//
        //zhejiang
        List<HashMap<String, String>> recordList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> recordMap = new HashMap<>();
        recordMap.put(COMPANYNAME, page.getHtml().xpath("/html/body/table/tbody/tr[2]/td/table[2]/tbody/tr[2]/td[3]/text(1)").toString().replaceAll("[\\u00A0]+", ""));
        recordMap.put(ANNOUNCEINSTITUTE, page.getHtml().xpath("/html/body/table/tbody/tr[2]/td/table[2]/tbody/tr[3]/td[3]/allText()").toString());
        recordMap.put(ANNCOUNCETYPE, page.getHtml().xpath("/html/body/table/tbody/tr[2]/td/table/tbody/tr/td/allText()").toString());
        recordMap.put(UPDATETIME, page.getHtml().xpath("/html/body/table/tbody/tr[2]/td/table[2]/tbody/tr[4]/td[3]/allText()").toString());
        recordList.add(recordMap);
        page.putField(BaseConstant.RESULT_DATA_KEY, recordList);
        page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("http://wwww.zjcredit.gov.cn\").setUserAgent(\"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36\")\n" +
                    "                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(7000)\n" +
                    "                    .setTimeOut(60000).setCharset(\"GB23.zjcredit.gov.cn").setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(7000)
                    .setTimeOut(60000).setCharset("GB2312");
        }
        return site;
    }

    public static void main(String[] args) {
        try {
            Spider.create(new AdminZhejiangProcessor())
                    .addUrl("http://www.zjcredit.gov.cn/sgs/sgsDetail.do?id=D62643AAE90351C7426F10D21589037833C529D912DDE303")
                    .thread(1)
                    .run();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
