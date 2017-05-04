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
 * @since 2016年12月7号
 * @see
 */
@Component("proxyIp181Processor")
public class ProxyIp181Processor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(getClass());
    private Site site;

    public ProxyIp181Processor() {
    }

    @Override
    public void process(Page page) {
        List<Selectable> trList = page.getHtml().xpath("//table/tbody/tr").nodes();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Selectable eachTr : trList) {
            // logger.error("======>>> eachTr:"+eachTr);
            List<Selectable> tdList = eachTr.xpath("//td//allText()").nodes();
            if(tdList.toString().contains("IP地址")){
                continue;
            }
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
        site = Site.me().setDomain("http://www.ip181.com").setRetryTimes(3).setCycleRetryTimes(3).setTimeOut(10000)
                .setSleepTime(CommonMethodUtils.randomSleepTime())
                .setUserAgent(CommonMethodUtils.getRandomUserAgent());
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new ProxyIp181Processor()).thread(1).addUrl("http://www.ip181.com").runAsync();
    }
}
