package com.fosun.fc.projects.creepers.pageprocessor.baidu;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
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
 * 信用江苏-法院失信被执行人
 * </p>
 *
 * @author Luxin 2017-3-9 14:15:33
 */
@Component
public class DishonestJiangsuProcessor extends BaseTaskListProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_CERT_NO = "certNo";
    private static final String COLUMN_CASE_CODE = "caseCode";
    private static final String COLUMN_CONTENT = "content";

    private Site site;

    @Override
    public void process(Page page) {

        CreepersParamDTO paramDTO = page.getResultItems().get(BaseConstant.PARAM_DTO_KEY);
        Request request = creepersTaskListServiceImpl.popRequest(paramDTO.getTaskType());
        if (request != null){
            page.addTargetRequest(request);
        }

        List<HashMap<String, Object>> recordList = new ArrayList<HashMap<String, Object>>();
        List<Selectable> divList = page.getHtml().xpath("/html/body/div/div[2]/div[2]/table/tbody/tr").nodes();
        for (int i=0;i<divList.size();i++){
            HashMap<String, Object> map = new HashMap<String,Object>();
            map.put(COLUMN_NAME, divList.get(i).xpath("//td[2]/a/allText()").toString());
            map.put(COLUMN_CERT_NO, divList.get(i).xpath("//td[3]/a/allText()").toString());
            map.put(COLUMN_CASE_CODE, divList.get(i).xpath("//td[5]/a/allText()").toString());
            map.put(COLUMN_CONTENT, "Jiangsu");
            recordList.add(map);
        }
        page.putField(CreepersConstant.TableNamesOthers.T_CREEPERS_DISHONEST_JIANGSU.getMapKey(), recordList);
        page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
        logger.info("======================>>>  DishonestJiangsuProcessor end!");
    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("http://218.94.123.13:9001\").setUserAgent(\"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36\")\n" +
                    "                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(7000)\n" +
                    "                    .setTimeOut(15000).setCharset(\"GB23.zjcredit.gov.cn").setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
                    .setRetryTimes(3).setCycleRetryTimes(3).setSleepTime(7000)
                    .setTimeOut(60000).setCharset("utf-8");
        }
        return site;
    }

    public static void main(String[] args) {
        try {
            Spider.create(new DishonestJiangsuProcessor())
                    .addUrl("http://218.94.123.13:9001/webapp/cms/sfysxbzxr_sfysxbzxrList.htm?dto.pageNo=2&query=&dto.name=&dto.cardNum=&checkCode=")
                    .thread(1)
                    .run();
        }catch (Exception e){
            System.out.println(e);
        }
    }
}
