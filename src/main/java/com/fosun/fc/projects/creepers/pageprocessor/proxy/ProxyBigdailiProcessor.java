package com.fosun.fc.projects.creepers.pageprocessor.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 
 * <p>
 * Demo: http://www.ip181.com/ 爬取 免费的proxy
 * </p>
 * 
 * @author LiZhanPing
 * @since 2017年3月2号
 * @see
 */
@Component
public class ProxyBigdailiProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(getClass());
    private Site site;

    public ProxyBigdailiProcessor() {
    }

    @Override
    public void process(Page page) {
        List<Selectable> trList = page.getHtml().xpath("//*[@id=\"nav_btn01\"]/div[6]//tbody/tr").nodes();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Selectable trSel : trList) {
            List<Selectable> tdList = trSel.xpath("//td/allText()").nodes();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(CreepersConstant.TCreepersProxyListColumn.IP.getValue(), tdList.get(0).toString());
            logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + tdList.get(0).toString());
            map.put(CreepersConstant.TCreepersProxyListColumn.PORT.getValue(), tdList.get(1).toString());
            logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + tdList.get(1).toString());
            map.put(CreepersConstant.TCreepersProxyListColumn.IP_TYPE.getValue(), tdList.get(3).toString());
            logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + tdList.get(3).toString());
            resultList.add(map);
        }
        page.putField(CreepersConstant.TableNamesOthers.T_CREEPERS_PROXY_LIST.getMapKey(), resultList);
    }

    @Override
    public Site getSite() {
        site = Site.me().setDomain("www.bigdaili.com").setRetryTimes(3).setCycleRetryTimes(3).setTimeOut(60000)
                .setSleepTime(CommonMethodUtils.randomSleepTime())
                .setUserAgent(CommonMethodUtils.getRandomUserAgent());
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new ProxyBigdailiProcessor()).thread(1).addUrl("http://www.bigdaili.com/dailiip/1/1.html#ip").runAsync();
    }
}
