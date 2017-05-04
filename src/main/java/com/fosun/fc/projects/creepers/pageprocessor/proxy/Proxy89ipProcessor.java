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
public class Proxy89ipProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(getClass());
    private Site site;

    public Proxy89ipProcessor() {
    }

    @Override
    public void process(Page page) {
        List<Selectable> textList = page.getHtml().xpath("//*[@class='mass']/allText()").nodes();
        String[] str = textList.toString().split(" ");
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (int i = 13;i<str.length-1;i++) {
            String[] ipInfo = str[i].split(":");
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(CreepersConstant.TCreepersProxyListColumn.IP.getValue(), ipInfo[0]);
            logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + ipInfo[0]);
            map.put(CreepersConstant.TCreepersProxyListColumn.PORT.getValue(), ipInfo[1]);
            logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + ipInfo[1]);
            resultList.add(map);
        }
        page.putField(CreepersConstant.TableNamesOthers.T_CREEPERS_PROXY_LIST.getMapKey(), resultList);
    }

    @Override
    public Site getSite() {
        site = Site.me().setDomain("www.89ip.cn").setRetryTimes(3).setCycleRetryTimes(3).setTimeOut(60000)
                .setSleepTime(CommonMethodUtils.randomSleepTime())
                .setUserAgent(CommonMethodUtils.getRandomUserAgent());
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new Proxy89ipProcessor()).thread(1).addUrl("http://www.89ip.cn/tiqu.php?sxb=&tqsl=10000&ports=&ktip=&xl=on&submit=%CC%E1++%C8%A1").runAsync();
    }
}
