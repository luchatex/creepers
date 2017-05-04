package com.fosun.fc.projects.creepers.pageprocessor.CFDA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.constant.CreepersConstant;
import com.fosun.fc.projects.creepers.downloader.HttpRequestDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.BaseCreepersListProcessor;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.TranslateMethodUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

@Component
public class MedicalGSPHNProcessor extends BaseCreepersListProcessor implements PageProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Site site;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:47.0) Gecko/20100101 Firefox/47.0";
    private static final String BASE_URL = "http://www.sda.gov.cn";
    private static final String ACCESS_SEARCH_URL = "/wbpp/generalsearch?sort=true&sortId=CTIME&columnid=CLID%7CBCID&relation=MUST%7CMUST&tableName=Region&colNum=2&qryNum=2&curPage=1&qryidstr=CLID%7CBCID&qryValue=cl0368%7C0018&record=20&mytarget=~blank&dateFormat=yyyy%C4%EAMM%D4%C2dd%C8%D5&titleLength=-1&subTitleFlag=0&classStr=%7C-1%7C-1%7CListColumnClass15%7CLawListSub15%7Clistnew15%7Clisttddate15%7Clistmore15%7Cdistance15%7Cclasstab15%7Cclasstd15%7CpageTdSTR15%7CpageTdSTR15%7CpageTd15%7CpageTdF15%7CpageTdE15%7CpageETd15%7CpageTdGO15%7CpageTdGOTB15%7CpageGOButton15%7CpageDatespan15%7Cpagestab15%7CpageGOText15";
    private static final String DETAIL_BASE_URL = "http://www.sda.gov.cn/WS01";
    private static final String RGX_SEARCH_URL = "/wbpp/generalsearch\\?sort=true&sortId=CTIME&columnid=CLID%7CBCID&relation=MUST%7CMUST&tableName=Region&colNum=2&qryNum=2&curPage=\\d+&qryidstr=CLID%7CBCID&qryValue=cl0368%7C0018&record=20&mytarget=~blank&dateFormat=yyyy%C4%EAMM%D4%C2dd%C8%D5&titleLength=-1&subTitleFlag=0&classStr=%7C-1%7C-1%7CListColumnClass15%7CLawListSub15%7Clistnew15%7Clisttddate15%7Clistmore15%7Cdistance15%7Cclasstab15%7Cclasstd15%7CpageTdSTR15%7CpageTdSTR15%7CpageTd15%7CpageTdF15%7CpageTdE15%7CpageETd15%7CpageTdGO15%7CpageTdGOTB15%7CpageGOButton15%7CpageDatespan15%7Cpagestab15%7CpageGOText15";
    private static final String RGX_DETAIL_URL = "/WS01/CL0368/\\d+.html";
    private static final String RGX_URL_TD = "//table[@class='classtab15']//tr/td/a/@href";

    private static final String KEY_TOTAL_PAGE = "totalPage";
    private static final String KEY_PAGE_NO = "pageNo";
    private static final String KEY_THREAD_NUM = "threadNum";

    @Override
    public void process(Page page) {
        logger.info("======================>>>  MedicalGSPHNProcessor start!");
        CreepersParamDTO param = page.getResultItems().get(BaseConstant.PARAM_DTO_KEY);
        String jsonRequest = JSON.toJSONString(page.getRequest());
        param.setBreakDownRequest(jsonRequest);
        try {
            if (page.getUrl().regex(BASE_URL + RGX_SEARCH_URL).match()) {
                List<Selectable> tdList = page.getHtml().xpath(RGX_URL_TD).nodes();
                for (Selectable tdSel : tdList) {
                    page.addTargetRequest(tdSel.toString().replace(BASE_URL, DETAIL_BASE_URL));
                    logger.info("=============>添加详情url：" + tdSel.toString().replace(BASE_URL, DETAIL_BASE_URL));
                }
                String totalPageStr = page.getHtml().xpath("//table[@class='pagestab15']//td/allText()").nodes().get(1)
                        .toString().trim();
                int totalPageCount = "".equals(totalPageStr) ? 0
                        : Integer.parseInt(
                                totalPageStr.substring(totalPageStr.indexOf("共") + 1, totalPageStr.lastIndexOf("页")));
                int threadNum = null == page.getRequest().getExtra(KEY_THREAD_NUM) ? 1
                        : (int) page.getRequest().getExtra(KEY_THREAD_NUM);
                int pageNo = null == page.getRequest().getExtra(KEY_PAGE_NO) ? 1
                        : (int) page.getRequest().getExtra(KEY_PAGE_NO);
                if (pageNo == 1) {
                    for (int j = 1; j <= threadNum; j++) {
                        if (pageNo + j <= totalPageCount) {
                            Request request = new Request(BASE_URL
                                    + ACCESS_SEARCH_URL.replace("curPage=" + pageNo, "curPage=" + (pageNo + j)));
                            request.putExtra(KEY_TOTAL_PAGE, totalPageCount);
                            request.putExtra(KEY_PAGE_NO, pageNo + threadNum);
                            request.putExtra(KEY_THREAD_NUM, threadNum);
                            page.addTargetRequest(request);
                            logger.info("=============>添加分页request：" + request);
                        }
                    }
                } else {
                    if (pageNo + threadNum <= totalPageCount) {
                        Request request = new Request(page.getUrl().toString().replace("curPage=" + pageNo, "curPage=" + (pageNo + threadNum)));
                        request.putExtra(KEY_TOTAL_PAGE, totalPageCount);
                        request.putExtra(KEY_PAGE_NO, pageNo + threadNum);
                        request.putExtra(KEY_THREAD_NUM, threadNum);
                        page.addTargetRequest(request);
                        logger.info("=============>添加分页request：" + request);
                    }
                }
                page.setSkip(true);
            } else if (page.getUrl().regex(RGX_DETAIL_URL).match()) {
                logger.info("===========GSP认证记录============");
                List<Map<String, String>> recordList = new ArrayList<Map<String, String>>();
                List<Selectable> trList = page.getHtml().xpath("//td[@class='articlecontent3']//table//tr").nodes();
                // 存储key值
                List<String> keyList = new ArrayList<String>();
                for (int i = 0; i < trList.size(); i++) {
                    List<Selectable> tdList = trList.get(i).xpath("//td/allText()").nodes();
                    if (tdList.toString().contains("GMP认证")) {
                    } else if (tdList.toString().replaceAll("\\s*", "").contains("企业名称")) {
                        for (Selectable tdSel : tdList){
                            if(tdSel.toString().replaceAll("\\s*", "").contains("认证范围"))
                                keyList.add("认证范围");
                            if(tdSel.toString().replaceAll("\\s*", "").contains("发证日期"))
                                keyList.add("认证日期");
                        }
                    } else {
                        HashMap<String, String> map = new HashMap<String, String>();
                        for (int j = 0; j < tdList.size(); j++) {
                            if ("企业名称".equals(CommonMethodUtils.matchesChineseValue(keyList.get(j)))) {
                                map.put(CreepersConstant.TCreepersMedicalColumn.KEY.getValue(), tdList.get(j).toString());
                                logger.info(CreepersConstant.TCreepersMedicalColumn.KEY.getValue() + ":" + tdList.get(j).toString());
                            }
                            map.put(keyList.get(j), tdList.get(j).toString());
                            logger.info(keyList.get(j) + ":" + tdList.get(j).toString());
                        }
                        map.put("省份", "湖南");
                        recordList.add(map);
                    }
                }
                TranslateMethodUtil.addCommonParamMapMedical(recordList, CreepersConstant.TCreepersMedicalColumn.TYPE.getValue(), BaseConstant.TaskListType.MEDICAL_GSP_LIST.getValue());
                page.getResultItems().put(BaseConstant.RESULT_DATA_KEY, recordList);
                logger.info(JSON.toJSONString(recordList));
            }
            logger.info("MedicalGSPHNProcessor process end!");
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("MedicalGSPHNProcessor process error:", e);
            String errorInfo = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
            param.setErrorInfo("MedicalGSPHNProcessor process error:" + errorInfo);
            param.setErrorPath(getClass().getName());
            creepersExceptionHandleServiceImpl.handleJobExceptionAndPrintLog(param);
            logger.error("======================>>>  MedicalGSPHNProcessor end!");
        }
    }

    @Override
    public Site getSite() {

        if (site == null) {
            site = Site.me().setDomain(BASE_URL).setUserAgent(USER_AGENT).setSleepTime(3000);
        }
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new MedicalGSPHNProcessor())
                .setDownloader(new HttpRequestDownloader().setParam(new CreepersParamDTO()))
                .addUrl(BASE_URL + ACCESS_SEARCH_URL).thread(1).run();
    }

}
