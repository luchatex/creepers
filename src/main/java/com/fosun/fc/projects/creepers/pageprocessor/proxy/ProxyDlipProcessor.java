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
public class ProxyDlipProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(getClass());
    private Site site;

    public ProxyDlipProcessor() {
    }

    @Override
    public void process(Page page) {
        List<Selectable> trList = page.getHtml().xpath("//*[@id=\"ip_list\"]/tbody/tr").nodes();
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Selectable trSel : trList) {
            Map<String, Object> map = new HashMap<String, Object>();
            if(trSel.regex("IP").match()){
                continue;
            }
            List<Selectable> tdList = trSel.xpath("//td//allText()").nodes();
            map.put(CreepersConstant.TCreepersProxyListColumn.IP.getValue(), tdList.get(1).toString());
            logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + tdList.get(1).toString());
            map.put(CreepersConstant.TCreepersProxyListColumn.PORT.getValue(), tdList.get(2).toString());
            logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + tdList.get(2).toString());
            map.put(CreepersConstant.TCreepersProxyListColumn.IP_TYPE.getValue(), tdList.get(5).toString());
            logger.info(CreepersConstant.TCreepersProxyListColumn.IP.getValue() + ":" + tdList.get(5).toString());
            resultList.add(map);
        }
        page.putField(CreepersConstant.TableNamesOthers.T_CREEPERS_PROXY_LIST.getMapKey(), resultList);
    }

    @Override
    public Site getSite() {
        site = Site.me().setDomain("www.dlip.cn").setRetryTimes(3).setCycleRetryTimes(3).setTimeOut(60000)
                .setSleepTime(CommonMethodUtils.randomSleepTime())
                .setUserAgent(CommonMethodUtils.getRandomUserAgent());
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new ProxyDlipProcessor()).thread(1).addUrl("http://www.dlip.cn/").runAsync();
    }
}
