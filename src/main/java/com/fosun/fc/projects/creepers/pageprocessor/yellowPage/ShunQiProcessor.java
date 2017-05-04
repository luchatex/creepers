package com.fosun.fc.projects.creepers.pageprocessor.yellowPage;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.downloader.HttpRequestDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.BaseMedicalProcessor;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.JdbcUtil;
import com.google.common.collect.Sets;
import com.virjar.dungproxy.client.util.PoolUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 
 * <p>
 * Demo: 顺企网 黄页 www.11467.com
 * </p>
 * 
 * @author MaXin
 * @since 2017-2-10 11:17:10
 * @see
 */
@Component("shunQiProcessor")
public class ShunQiProcessor extends BaseMedicalProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private Site site;

    public ShunQiProcessor() {
        if (null == site) {
            site = Site.me().setDomain("www.11467.com")
                    .setUserAgent(
                            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36")
                    .setRetryTimes(3).setCycleRetryTimes(3).setAcceptStatCode(Sets.newHashSet(200, 404))
                    .setSleepTime(CommonMethodUtils.randomSleepTime()).setTimeOut(60000);
        }
    }

    @Override
    public void process(Page page) {
        // 新增下一个链接任务
        logger.info("从redis中获取请求-开始");
        Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.COMPANY_LIST.getValue());
        logger.info("从redis中获取请求-结束");
        if (request != null) {
            page.addTargetRequest(request);
        }
        logger.info("========================>>ShunQiProcessor:  start");
        if (StringUtils.isBlank(page.getRawText())) {
            logger.info("addToCycleRetry 处理过, page内容为空,不需要解析，直接跳过。");
            page.setSkip(true);
            return;
        }
        ResultItems resultItem = page.getResultItems();
        Map<String, String> map = new HashMap<String, String>();
        resultItem.put(CreepersConstant.TableNamesOthers.T_CREEPERS_COMPANY_LIST.getMapKey(), map);

        if (page.getHtml().regex("页面不存在").match() || 404 == page.getStatusCode()) {
            logger.info("指定的页面不存在!  url:" + page.getUrl().toString());
            page.setSkip(true);
            logger.info("========================>>ShunQiProcessor:  end");
            String updateSQL = "update t_creepers_task_list t set t.flag = '4' where t.url = '"
                    + page.getRequest().getUrl() + "'";
            try {
                JdbcUtil.execute(updateSQL);
            } catch (ClassNotFoundException e) {
                logger.error("JdbcUtil.execute  ClassNotFoundException!");
                e.printStackTrace();
            } catch (SQLException e) {
                logger.error("JdbcUtil.execute  SQLException!");
                e.printStackTrace();
            }
            return;
        } else if (page.getHtml().regex("Panel Komunikacyjny").match() || page.getHtml().regex("intervalle").match()) {
            PoolUtil.offline(page.getResultItems().get(BaseConstant.HTTP_CLIENT_CONTEXT));
            page.setSkip(true);
            return;
        }
        List<Selectable> navs = page.getHtml().xpath("//*[@class='navleft']//a").nodes();
        if (navs.size() <= 0) {
            page.setSkip(true);
            return;
        }
        String name = navs.get(navs.size() - 1).xpath("allText()").toString();
        // 企业名称
        map.put(CreepersConstant.TCreepersCompanyListColumn.NAME.getValue(), name);
        logger.info(CreepersConstant.TCreepersCompanyListColumn.NAME.getValue() + ":"
                + map.get(CreepersConstant.TCreepersCompanyListColumn.NAME.getValue()));

        // 数据对应的url
        map.put(CreepersConstant.TCreepersCompanyListColumn.MEMO.getValue(), page.getUrl().toString());
        logger.info(CreepersConstant.TCreepersCompanyListColumn.MEMO.getValue() + ":"
                + map.get(CreepersConstant.TCreepersCompanyListColumn.MEMO.getValue()));

        List<Selectable> dlList = page.getHtml().xpath("//*[@id=\"gongshang\"]//dl/").nodes();
        // 工商信息描述
        map.put(CreepersConstant.TCreepersCompanyListColumn.BASE_INFO.getValue(), dlList.toString());
        logger.debug(CreepersConstant.TCreepersCompanyListColumn.BASE_INFO.getValue() + ":"
                + map.get(CreepersConstant.TCreepersCompanyListColumn.BASE_INFO.getValue()));

        String lastContent = StringUtils.EMPTY;
        for (Selectable eachdd : dlList) {
            String currentContent = eachdd.xpath("//allText()").toString().replace("企业网", "");
            logger.debug("======>>> each dl" + currentContent);
            if (lastContent.contains("所属分类")) {
                // 所属行业
                map.put(CreepersConstant.TCreepersCompanyListColumn.INDUSTRY_TYPE.getValue(), currentContent);
                logger.info("currentContent(所属分类):" + currentContent);
            } else if (lastContent.contains("所属城市")) {
                // 地域
                map.put(CreepersConstant.TCreepersCompanyListColumn.AREA.getValue(), currentContent);
                logger.info("currentContent(所属城市):" + currentContent);
            }
            lastContent = currentContent;
        }

        if (StringUtils.isBlank(map.get(CreepersConstant.TCreepersCompanyListColumn.AREA.getValue()))
                || StringUtils.isBlank(map.get(CreepersConstant.TCreepersCompanyListColumn.NAME.getValue()))) {
            page.setSkip(true);
            logger.warn("====>>>>  姓名或区域名字为空，不能进行入库!");
        }
        page.putField(BaseConstant.RESULT_DATA_KEY, map);
        page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
        logger.debug(resultItem.get(BaseConstant.RESULT_DATA_KEY).toString());
        logger.info("========================>>ShunQiProcessor:  end");
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String threadNum = "1";
        CreepersParamDTO param = new CreepersParamDTO();
        param.setTaskType(BaseConstant.TaskListType.COMPANY_LIST.getValue());
        param.setTaskStatus(BaseConstant.TaskListStatus.DEFAULT.getValue());
        Request request = new Request("http://www.11467.com/shenzhen/co/893163.htm");
        Spider.create(new ShunQiProcessor()).setDownloader(new HttpRequestDownloader().setParam(param))
                .addRequest(request).thread(Integer.valueOf(threadNum)).runAsync();
    }
}
