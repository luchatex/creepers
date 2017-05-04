package com.fosun.fc.projects.creepers.pageprocessor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.downloader.TianYanChaSeleniumDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.CFDA.BaseMedicalProcessor;
import com.fosun.fc.projects.creepers.pipeline.HtmlToJsonFilePipline;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 
 * <p>
 * 天眼查
 * </p>
 * 
 * @author MaXin
 * @since 2016-7-18 16:03:44
 * @see
 */
@Component("tianYanChaProcessor")
public class TianYanChaProcessor extends BaseMedicalProcessor implements PageProcessor {

    private static final String SPILT_COLON = ":";

    private Site site;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public TianYanChaProcessor() {
        if (null == site) {
            site = Site.me().setDomain("www.tianyancha.com").setTimeOut(60000).setSleepTime(1000)
                    .setUserAgent(CommonMethodUtils.getRandomUserAgent());
        }

    }

    @Override
    public void process(Page page) {
        logger.info("========================>>TianYanChaProcessor:  start");
        // 新增下一个链接任务
        Request request = creepersTaskListServiceImpl.popRequest(BaseConstant.TaskListType.TYC_BASE_INFO.getValue());
        if (request != null) {
            page.addTargetRequest(request);
        }
        CreepersParamDTO param = page.getResultItems().get(BaseConstant.PARAM_DTO_KEY);

        Map<String, String> map = new HashMap<String, String>();
        page.putField(CreepersConstant.TableNamesOthers.T_CREEPERS_TYC_BASE_INFO.getMapKey(), map);

        if (page.getRawText().equals("没有找到相关结果")) {
            page.setSkip(true);
            return;
        }

        // 公司名称
        Selectable nameInfoDiv = page.getHtml().xpath("//*[@class=\"company_info_text\"]/p/text()");
        if (nameInfoDiv.toString().contains("<span")) {
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.NAME.getValue(), nameInfoDiv.toString().substring(0, nameInfoDiv.toString().indexOf("<span")).trim());
        } else {
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.NAME.getValue(), nameInfoDiv.toString().trim());
        }
        logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.NAME.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.NAME.getValue()));

        param.putSearchKeyWord(map.get(CreepersConstant.TCreepersTycBaseInfoColumn.NAME.getValue()));

        Selectable baseInfoDiv = page.getHtml().xpath("//*[@class=\"row b-c-white company-content\"]");
        if (baseInfoDiv != null) {
            logger.debug("======>>> baseInfoDiv:" + baseInfoDiv.toString());

            // 基本信息 table1
            List<Selectable> tdTextList1 = baseInfoDiv.xpath("//table[1]//td").nodes();
            logger.debug("tdTextList1:" + tdTextList1.toString());

            // 法定代表人
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.LEGAL_REPRESENTATIVE.getValue(), tdTextList1.get(3).xpath("//*[@class=\"ng-binding ng-scope\"]/allText()").toString());
            logger.info(
                    CreepersConstant.TCreepersTycBaseInfoColumn.LEGAL_REPRESENTATIVE.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.LEGAL_REPRESENTATIVE.getValue()));

            // 注册资本
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTERED_CAPITAL.getValue(), tdTextList1.get(4).xpath("//allText()").toString());
            logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTERED_CAPITAL.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTERED_CAPITAL.getValue()));

            // 开业状态
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.OPENING_STATE.getValue(), tdTextList1.get(7).xpath("//allText()").toString());
            logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.OPENING_STATE.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.OPENING_STATE.getValue()));

            // 注册时间
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTRATION_TIME.getValue(), tdTextList1.get(8).xpath("//allText()").toString());
            logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTRATION_TIME.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTRATION_TIME.getValue()));

            // 基本信息 table2
            List<Selectable> tdTextList2 = baseInfoDiv.xpath("//table[2]//td/div/span/allText()").nodes();
            logger.debug("tdTextList2:" + tdTextList2.toString());

            // 行业
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.TRADE.getValue(), tdTextList2.get(0).toString());
            logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.TRADE.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.TRADE.getValue()));

            // 工商注册号
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.BUSI_ID.getValue(), tdTextList2.get(1).toString());
            logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.BUSI_ID.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.BUSI_ID.getValue()));

            // 企业类型
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.ENTERPRISE_TYPE.getValue(), tdTextList2.get(2).toString());
            logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.ENTERPRISE_TYPE.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.ENTERPRISE_TYPE.getValue()));

            // 组织机构代码
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.ORGANIZATION_CODE.getValue(), tdTextList2.get(3).toString());
            logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.ORGANIZATION_CODE.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.ORGANIZATION_CODE.getValue()));

            if (tdTextList2.size() > 4) {
                // 营业期限
                map.put(CreepersConstant.TCreepersTycBaseInfoColumn.OPERATING_PERIOD.getValue(), tdTextList2.get(4).toString());
                logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.OPERATING_PERIOD.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.OPERATING_PERIOD.getValue()));
            }

            if (tdTextList2.size() > 5) {
                // 登记机关
                map.put(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTRATION_AUTHORITY.getValue(), tdTextList2.get(5).toString());
                logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTRATION_AUTHORITY.getValue()
                        + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTRATION_AUTHORITY.getValue()));
            }

            if (tdTextList2.size() > 6) {
                // 核准日期
                map.put(CreepersConstant.TCreepersTycBaseInfoColumn.APPROVED_DATE.getValue(), tdTextList2.get(6).toString());
                logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.APPROVED_DATE.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.APPROVED_DATE.getValue()));
            }

            if (tdTextList2.size() > 7) {
                // 统一信用代码
                map.put(CreepersConstant.TCreepersTycBaseInfoColumn.UNIFORM_CREDIT_CODE.getValue(), tdTextList2.get(7).toString());
                logger.info(
                        CreepersConstant.TCreepersTycBaseInfoColumn.UNIFORM_CREDIT_CODE.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.UNIFORM_CREDIT_CODE.getValue()));
            }

            if (tdTextList2.size() > 8) {
                // 注册地址
                map.put(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTERED_ADDRESS.getValue(), tdTextList2.get(8).toString());
                logger.info(
                        CreepersConstant.TCreepersTycBaseInfoColumn.REGISTERED_ADDRESS.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.REGISTERED_ADDRESS.getValue()));
            }

            if (tdTextList2.size() > 9) {
                // 经营范围
                map.put(CreepersConstant.TCreepersTycBaseInfoColumn.BUSINESS_SCOPE.getValue(), tdTextList2.get(9).toString());
                logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.BUSINESS_SCOPE.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.BUSINESS_SCOPE.getValue()));
            }
            // html的json文件名
            map.put(CreepersConstant.TCreepersTycBaseInfoColumn.FILE_NAME.getValue(), map.get(CreepersConstant.TCreepersTycBaseInfoColumn.NAME.getValue()) + BaseConstant.FileSuffix.JSON.getValue());
            logger.info(CreepersConstant.TCreepersTycBaseInfoColumn.FILE_NAME.getValue() + SPILT_COLON + map.get(CreepersConstant.TCreepersTycBaseInfoColumn.FILE_NAME.getValue()));

            page.putField(BaseConstant.RESULT_DATA_KEY, page.getHtml().toString());
            page.putField(BaseConstant.JSON_FILE_NAME, map.get(CreepersConstant.TCreepersTycBaseInfoColumn.NAME.getValue()));
            page.putField(BaseConstant.CURRENT_URL, page.getUrl().toString());
            logger.debug("resultItems:" + page.getResultItems().toString());
        } else {
            logger.info("======>>> baseInfoDiv is null!!!");
            logger.info("======>>> page html:" + page.getHtml().toString());
        }
        logger.info("TianYanChaProcessor end!");
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new TianYanChaProcessor()).setDownloader(new TianYanChaSeleniumDownloader())
                .addUrl("http://www.tianyancha.com/search?key=量富征信管理有限公司&checkFrom=searchBox")
                .addPipeline(new HtmlToJsonFilePipline()).thread(1).runAsync();
    }
}
