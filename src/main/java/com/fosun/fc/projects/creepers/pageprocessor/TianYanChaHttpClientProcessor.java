package com.fosun.fc.projects.creepers.pageprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.SimulateLoginDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersLoginParamDTO;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 
 * <p>
 * 天眼查
 * </p>
 * 
 * @author MaXin
 * @since 2017-1-10 15:03:44
 * @see
 */
@Component("tianYanChaHttpClientProcessor")
public class TianYanChaHttpClientProcessor implements PageProcessor {

    private Site site;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public TianYanChaHttpClientProcessor() {
        if (null == site) {
            site = Site.me().setDomain("www.tianyancha.com").setTimeOut(60000).setUserAgent(CommonMethodUtils.getRandomUserAgent())
                    .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                    .addHeader("Cache-Control", "max-age=10000")
                    .addHeader("Connection", "keep-alive")
                    ;
        }

    }

    @Override
    public void process(Page page) {
        logger.info("TianYanChaHttpClientProcessor ======>>> start!");
        if (page.getUrl().regex("http://www.tianyancha.com/company/766785046").match() && !page.getUrl().toString().endsWith(".json")) {
            logger.info("currentPage html:" + page.getHtml());
            page.addTargetRequest("http://www.tianyancha.com/company/766785046.json");
        } else if (page.getUrl().regex("http://www.tianyancha.com/company/766785046.json").match()) {
            logger.info("currentPage json:" + page.getJson());
            logger.info("get json Succeed!");
        } 
            
        logger.info("TianYanChaHttpClientProcessor ======>>> end!");
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        CreepersLoginParamDTO param = new CreepersLoginParamDTO();
        param.putOrderUrl(BaseConstant.OrderUrlKey.CAPTCHA_URL,"nothing!");
        Spider.create(new TianYanChaHttpClientProcessor()).setDownloader(new SimulateLoginDownloader().setParam(param))
//        .addUrl("http://www.tianyancha.com/search?key=%E6%A2%A7%E5%B7%9E%E4%B8%AD%E8%8C%B6%E8%8C%B6%E4%B8%9A%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8&checkFrom=searchBox")
//        .addUrl("http://www.tianyancha.com/search/%E6%A2%A7%E5%B7%9E%E4%B8%AD%E8%8C%B6%E8%8C%B6%E4%B8%9A%E6%9C%89%E9%99%90%E5%85%AC%E5%8F%B8.json?")
        .addUrl("http://www.tianyancha.com/company/766785046")
//        .addUrl("http://www.tianyancha.com/company/766785046.json")
         .thread(1).runAsync();
    }
}
