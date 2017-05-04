package com.fosun.fc.projects.creepers.pageprocessor.proxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class ProxyArpunProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(getClass());
    private Site site;

    public ProxyArpunProcessor() {
    }

    @Override
    public void process(Page page) {
        List<Selectable> strList = page.getHtml().xpath("//*[@id=\"NewsContentLabel\"]/p/span/allText()").nodes();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Selectable strSel : strList) {
            Map<String, Object> map = new HashMap<String, Object>();
            if(strSel.regex("参考消息").match()){
                continue;
            }
            Pattern pattern = Pattern.compile("([0-9.]+):(\\d+)@(\\w+)#");
            Matcher matcher = pattern.matcher(strSel.toString());
            if(matcher.find()){
                map.put(CreepersConstant.TCreepersProxyListColumn.IP.getValue(), matcher.group(1));
                logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + matcher.group(1));
                map.put(CreepersConstant.TCreepersProxyListColumn.PORT.getValue(), matcher.group(2));
                logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + matcher.group(2));
                map.put(CreepersConstant.TCreepersProxyListColumn.IP_TYPE.getValue(), matcher.group(3));
                logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + matcher.group(3));
                resultList.add(map);
            }
        }
        page.putField(CreepersConstant.TableNamesOthers.T_CREEPERS_PROXY_LIST.getMapKey(), resultList);
    }

    @Override
    public Site getSite() {
        site = Site.me().setDomain("www.arpun.com").setRetryTimes(3).setCycleRetryTimes(3).setTimeOut(60000)
                .setSleepTime(CommonMethodUtils.randomSleepTime())
                .setUserAgent(CommonMethodUtils.getRandomUserAgent());
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new ProxyArpunProcessor()).thread(1).addUrl("http://www.arpun.com/article/10094.html").runAsync();
    }
}
