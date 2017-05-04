package com.fosun.fc.projects.creepers.pageprocessor.CreditChina;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.BaseTaskListProcessor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/***
 * 
 * <p>
 * 信用中国-行政处罚北京
 * </p>
 * 
 * @author Luxin 2017-2-8 13:30:33
 */
@Component
public class AdminBeijingProcessor extends BaseTaskListProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    
    //公司名
    public final static String COMPANYNAME="companyName";
    //处罚类型
    public final static String ANNCOUNCETYPE="announceType";
    //委办局
    public final static String ANNOUNCEINSTITUTE="announceInstitute";
    //发布时间
    public final static String UPDATETIME="updateTime";

    private Site site;

    public static String INDEX_URL = "http://cxcj.creditbj.gov.cn/sonic/business/powerList/doubleList/double_list.shtml?dataSign=Y2FwaW5mbzEyMw%3D%3D&showType=%E8%A1%8C%E6%94%BF%E5%A4%84%E7%BD%9A&text=&page=";

    @Override
    public void process(Page page) {
    	
    	CreepersParamDTO paramDTO = page.getResultItems().get(BaseConstant.PARAM_DTO_KEY);
    	Request request = creepersTaskListServiceImpl.popRequest(paramDTO.getTaskType());
    	if (request != null){
    		page.addTargetRequest(request);
    	}

    	List<HashMap<String, String>> recordList = new ArrayList<HashMap<String, String>>();
    	List<Selectable> divList =  page.getHtml().xpath("//*[@id='menu_yhm']/a").nodes();
    	
    	//遍历每页的记录
    	for (int i = 0; i < divList.size(); i++) {
    		HashMap<String, String> recordMap = new HashMap<String, String>();
    		recordMap.put(COMPANYNAME, divList.get(i).xpath("//a/div/div[1]/@title").toString());
    		recordMap.put(ANNCOUNCETYPE, divList.get(i).xpath("//a/div/div[2]/@title").toString());
    		recordMap.put(ANNOUNCEINSTITUTE, divList.get(i).xpath("//a/div/div[3]/@title").toString());
    		recordMap.put(UPDATETIME, divList.get(i).xpath("//a/div/div[4]/allText()").toString());
    		recordList.add(recordMap);
		}
    	page.putField(BaseConstant.RESULT_DATA_KEY, recordList);
    	page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("http://cxcj.creditbj.gov.cn").setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(7000)
                    .setTimeOut(60000).setCharset("UTF-8");
        }
        return site;
    }

    public static void main(String[] args) throws IOException {
       Spider.create(new AdminBeijingProcessor()).addUrl(INDEX_URL+"1").thread(1).run();
    }
}
