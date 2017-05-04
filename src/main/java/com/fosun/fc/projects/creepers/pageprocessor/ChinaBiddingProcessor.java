package com.fosun.fc.projects.creepers.pageprocessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.google.common.collect.Sets;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.utils.HttpConstant;

/***
 * 
 * <p>
 * 中国国际招标网
 * </p>
 * 
 * @author LiZhanPing 2017-1-11
 */
@Component("chinaBiddingProcessor")
public class ChinaBiddingProcessor extends BaseCreepersListProcessor implements PageProcessor {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String poClass[] = { "poClass" };
    public static final Set<String> poClassSet = Sets.newHashSet(poClass);
    // 信息类型
    public static final String infoClassCode[] = { "", "0105", "0106", "0107", "0108" };
    public static final Set<String> infoClassCodeSet = Sets.newHashSet(infoClassCode);
    // 发布时间
    public static final String pubDate[] = { "" };
    public static final Set<String> pubDateSet = Sets.newHashSet(pubDate);
    // 范围
    public static final String rangeType[] = { "", "1", "2" };
    public static final Set<String> rangeTypeSet = Sets.newHashSet(rangeType);
    public static final List<String> rangeTypeList = Arrays.asList(rangeType);
    // 行业
    public static final String normIndustry[] = { "", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11",
            "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
            "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47",
            "48", "49", "50" };
    public static final Set<String> normIndustrySet = Sets.newHashSet(normIndustry);
    public static final List<String> normIndustryList = Arrays.asList(normIndustry);
    // 区域
    public static final String zoneCode[] = { "", "11*", "12*", "13*", "14*", "15*", "21*", "22*", "23*", "31*", "32*",
            "33*", "34*", "35*", "37*", "36*", "41*", "42*", "43*", "44*", "45*", "46*", "50*", "51*", "52*", "53*",
            "54*", "61*", "62*", "63*", "64*", "65*", "71*", "81*", "82*" };
    public static final Set<String> zoneCodeSet = Sets.newHashSet(zoneCode);
    public static final List<String> zoneCodeList = Arrays.asList(zoneCode);
    // 资金来源
    public static final String fundSourceCodes[] = { "", "010201", "010203", "03", "0101*", "07" };
    public static final Set<String> fundSourceCodeSet = Sets.newHashSet(fundSourceCodes);
    public static final List<String> fundSourceCodeList = Arrays.asList(fundSourceCodes);
    // 搜索关键字
    public static final String fullText[] = { "", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009",
            "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017" };
    public static final Set<String> fullTextSet = Sets.newHashSet(fullText);
    public static final List<String> fullTextList = Arrays.asList(fullText);

    public static final String groups[] = { "zoneCode", "normIndustry", "rangeType", "fullText", "fundSourceCodes" };
    public static final List<String> groupList = Arrays.asList(groups);

    private Site site;

    @SuppressWarnings("unchecked")
    @Override
    public void process(Page page) {
        try {
            Map<String, Object> arrayMap = new HashMap<String, Object>();
            arrayMap.put("zoneCode", zoneCode);
            arrayMap.put("normIndustry", normIndustry);
            arrayMap.put("rangeType", rangeType);
            arrayMap.put("fullText", fullText);
            arrayMap.put("fundSourceCodes", fundSourceCodes);
            Map<String, Object> listMap = new HashMap<String, Object>();
            listMap.put("zoneCode", zoneCodeList);
            listMap.put("normIndustry", normIndustryList);
            listMap.put("rangeType", rangeTypeList);
            listMap.put("fullText", fullTextList);
            listMap.put("fundSourceCodes", fundSourceCodeList);
            // logger.info("page:" + page.getHtml());
            String val = page.getHtml().xpath("//*[@id=\"lab-show\"]/div[2]/div[1]/h3/div/ul/li/span/text()")
                    .toString();
            List<Integer> yTotal = (List<Integer>) page.getRequest().getExtra("yTotal");
            List<Integer> nTotal = (List<Integer>) page.getRequest().getExtra("nTotal");
            List<String> x = (List<String>) page.getRequest().getExtra("x");
            String indexUrl = page.getRequest().getUrl();
            indexUrl = indexUrl.substring(0, indexUrl.indexOf("t=")) + "t=" + new Date().getTime();
            Request request = new Request(indexUrl);
            request.setMethod(HttpConstant.Method.POST);
            NameValuePair[] newNameValuePairs;
            String group = "";
            Integer realSum = 0;
            Integer ySum = 0;
            Integer nSum = 0;
            Integer yPerSum = 0;
            NameValuePair[] nameValuePairs = (NameValuePair[]) page.getRequest()
                    .getExtra(BaseConstant.POST_NAME_VALUE_PAIR);
            for (int i = 0; i < nameValuePairs.length; i++) {
                if ("normIndustry".equals(nameValuePairs[i].getName())&&"".equals(nameValuePairs[i].getValue())) {
                    yTotal.add(-Integer.valueOf(val));
                }
            }
            if (Integer.valueOf(val) > 1000) {
                group = zf(nameValuePairs);
                // 如果该参数下边不存在次要参数，则该参数的值取对应数组的下一个，如果原来的值是最后一个的话，
                // 则该参数的值置空，该参数上边的更重要参数的值取对应数组的下一个，如果该参数上边不存在
                // 更主要的参数，则结束整个流程
                if ("".equals(group)) {
                    group = "fundSourceCodes";
                    yTotal.add(-1);
                    nTotal.add(Integer.valueOf(val));
                    logger.info("nVal:" + val);
                    x.add(JSON.toJSONString(nameValuePairs));
                    logger.debug("nameValuePairs:" + JSON.toJSONString(nameValuePairs));
                    newNameValuePairs = recf(nameValuePairs, listMap, groupList, group);
                    logger.info("newNameValuePairs:" + JSON.toJSONString(newNameValuePairs));
                } else {
                    logger.debug("nVal:" + val);
                    logger.debug("nameValuePairs:" + JSON.toJSONString(nameValuePairs));
                    String[] array = (String[]) arrayMap.get(group);
                    newNameValuePairs = copyAndModifyNameValuePair(nameValuePairs, group, array[1]);
                    logger.debug("newNameValuePairs:" + JSON.toJSONString(newNameValuePairs));
                }
            } else {
                yTotal.add(Integer.valueOf(val));
                logger.info("yVal:" + val);
                logger.info("nameValuePairs:" + JSON.toJSONString(nameValuePairs));
                group = zf(nameValuePairs);
                if ("".equals(group)) {
                    group = "fundSourceCodes";
                } else {
                    group = previousSbiling(groupList, group);
                }
                newNameValuePairs = recf(nameValuePairs, listMap, groupList, group);
                logger.debug("newNameValuePairs:" + JSON.toJSONString(newNameValuePairs));
            }
            if (null != newNameValuePairs) {
                request.putExtra(BaseConstant.POST_NAME_VALUE_PAIR, newNameValuePairs);
                request.putExtra("yTotal", yTotal);
                request.putExtra("nTotal", nTotal);
                request.putExtra("x", x);
                page.addTargetRequest(request);
                int j = 0;
                int m=0;
                logger.debug("=============================详情=============================");
                for (int i = 0; i < yTotal.size(); i++) {
                    if (yTotal.get(i) > -1) {
                        ySum = ySum + yTotal.get(i);
                        yPerSum = yPerSum  + yTotal.get(i);
                    }else if (yTotal.get(i) == -1) {
                        j++;
                    }else {
                        if(i!=0){
                            logger.debug("第"+m+"地方已发现的数据条数"+(yPerSum+j*1000));
                        }
                        m++;
                        logger.debug("第"+m+"地方待发现的数据条数"+(-yTotal.get(i)));
                        yPerSum = 0 ;
                        j = 0;
                    }
                    if (i==yTotal.size()-1) {
                        logger.debug("第"+m+"地方已发现的数据条数"+(yPerSum+j*1000));
                    }
                }
                for (int i = 0; i < nTotal.size(); i++)
                    nSum = nSum + nTotal.get(i);
                logger.debug("=============================汇总=============================");
                logger.debug("需要发现的数据条数： 2213332");
                logger.debug("已发现的数据条数：" + (ySum+nTotal.size()*1000));
                logger.debug("可能发现的数据条数：" + (nSum - nTotal.size() * 1000));
                logger.debug("可能发现的数据的参数的个数：" + x.size());
            } else {
                for (int i = 0; i < yTotal.size(); i++) {
                    if (yTotal.get(i) > 0) {
                        ySum = ySum + yTotal.get(i);
                    } else {
                        realSum = realSum - yTotal.get(i);
                    }
                }
                for (int i = 0; i < nTotal.size(); i++)
                    nSum = nSum + nTotal.get(i);
                logger.info("未处理的可发现的数据条数：" + (ySum + nTotal.size() * 1000));
                ySum = ySum - (ySum + nSum - realSum) + nTotal.size() * 1000;
                logger.info("可发现的数据条数：" + ySum);
                logger.info("可能发现的数据条数：" + (nSum - nTotal.size() * 1000));
                logger.info("可能发现的数据的参数的个数：" + x.size());
                logger.info("可能发现的数据的参数：" + JSON.toJSONString(x));
            }
            page.setSkip(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 获取更次要的条件的位置
    public static String zf(NameValuePair[] nameValuePairs) {
        String zf = "";
        if (fundSourceCodes[0].equals(getValue(nameValuePairs, "fundSourceCodes"))) {
            if (fullText[0].equals(getValue(nameValuePairs, "fullText"))) {
                if (rangeType[0].equals(getValue(nameValuePairs, "rangeType"))) {
                    if (normIndustry[0].equals(getValue(nameValuePairs, "normIndustry"))) {
                        if (zoneCode[0].equals(getValue(nameValuePairs, "zoneCode"))) {
                            zf = "zoneCode";
                        } else {
                            zf = "normIndustry";
                        }
                    } else {
                        zf = "rangeType";
                    }
                } else {
                    zf = "fullText";
                }
            } else {
                zf = "fundSourceCodes";
            }
        }
        return zf;
    }

    public static String previousSbiling(List<String> list, String o) {
        if (null != list && list.size() > 0 && o != null && list.contains(o)) {
            Integer index = list.indexOf(o);
            if (index == 0) {
                return null;
            } else {
                return list.get(index - 1);
            }
        } else {
            return null;
        }
    }

    public static String nextSbiling(List<String> list, String o) {
        if (null != list && list.size() > 0 && o != null && list.contains(o)) {
            Integer index = list.indexOf(o);
            if (index == list.size() - 1) {
                return null;
            } else {
                return list.get(index + 1);
            }
        } else {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static NameValuePair[] recf(NameValuePair[] nameValuePairs, Map<String, Object> listMap, List<String> groupList,
            String group) {
        NameValuePair[] newNameValuePair = null;
        if (null != nameValuePairs && null != listMap && listMap.size() > 0 && null != groupList && groupList.size() > 0
                && null != group) {
            if (cf(nameValuePairs, group)) {
                String previousGroup = previousSbiling(groupList, group);
                if (null == previousGroup) {
                    return null;
                } else {
                    newNameValuePair = copyAndModifyNameValuePair(nameValuePairs, group, "");
                    return recf(newNameValuePair, listMap, groupList, previousGroup);
                }
            } else {
                newNameValuePair = copyAndModifyNameValuePair(nameValuePairs, group,
                        nextSbiling((List<String>) listMap.get(group), getValue(nameValuePairs, group)));
                return newNameValuePair;
            }
        } else {
            return newNameValuePair;
        }
    }

    public static Boolean cf(NameValuePair[] nameValuePairs, String key) {
        Boolean cf = false;
        if ("fundSourceCodes".equals(key))
            if (fundSourceCodes[fundSourceCodes.length - 1].equals(getValue(nameValuePairs, "fundSourceCodes")))
                cf = true;
        if ("fullText".equals(key))
            if (fullText[fullText.length - 1].equals(getValue(nameValuePairs, "fullText")))
                cf = true;
        if ("rangeType".equals(key))
            if (rangeType[rangeType.length - 1].equals(getValue(nameValuePairs, "rangeType")))
                cf = true;
        if ("normIndustry".equals(key))
            if (normIndustry[normIndustry.length - 1].equals(getValue(nameValuePairs, "normIndustry")))
                cf = true;
        if ("zoneCode".equals(key))
            if (zoneCode[zoneCode.length - 1].equals(getValue(nameValuePairs, "zoneCode")))
                cf = true;
        return cf;
    }

    public static String getValue(NameValuePair[] nameValuePairs, String key) {
        String result = "";
        if (null != nameValuePairs && nameValuePairs.length > 0 && StringUtils.isNoneBlank(key)) {
            for (int i = 0; i < nameValuePairs.length; i++) {
                if (key.equals(nameValuePairs[i].getName())) {
                    result = nameValuePairs[i].getValue();
                }
            }
        }
        return result;
    }

    public static NameValuePair[] copyAndModifyNameValuePair(NameValuePair[] nameValuePairs, String key, String value) {
        NameValuePair[] newNameValuePairs = new NameValuePair[nameValuePairs.length];
        if (null != nameValuePairs && nameValuePairs.length > 0) {
            if (StringUtils.isNoneBlank(key)) {
                for (int i = 0; i < newNameValuePairs.length; i++) {
                    if (key.equals(nameValuePairs[i].getName())) {
                        newNameValuePairs[i] = new BasicNameValuePair(key, value);
                    } else {
                        newNameValuePairs[i] = new BasicNameValuePair(nameValuePairs[i].getName(),
                                nameValuePairs[i].getValue());
                    }
                }
            } else {
                newNameValuePairs = nameValuePairs;
            }
        }
        return newNameValuePairs;
    }

    @Override
    public Site getSite() {
        if (null == site) {
            site = Site.me().setDomain("www.chinabidding.com").setUserAgent(CommonMethodUtils.getRandomUserAgent())
                    .setRetryTimes(10).setCycleRetryTimes(5).setSleepTime(11 * 697).setTimeOut(90000)
                    .setCharset("UTF-8");
        }
        return site;
    }

    public static void main(String[] args) throws IOException {
        List<Integer> yTotal = new ArrayList<Integer>();
        List<Integer> nTotal = new ArrayList<Integer>();
        List<String> x = new ArrayList<String>();
        String url = "http://www.chinabidding.com/search/proj.htm" + "?t=" + new Date().getTime();
        Request request = new Request(url);
        request.setMethod(HttpConstant.Method.POST);
        NameValuePair[] nameValuePair = new NameValuePair[8];
        nameValuePair[0] = new BasicNameValuePair("poClass", "BidResult");
        nameValuePair[1] = new BasicNameValuePair("infoClassCodes", "0108");
        nameValuePair[2] = new BasicNameValuePair("pubDate", "");
        nameValuePair[3] = new BasicNameValuePair("rangeType", "");
        nameValuePair[4] = new BasicNameValuePair("normIndustry", "");
        nameValuePair[5] = new BasicNameValuePair("zoneCode", "11*");
        nameValuePair[6] = new BasicNameValuePair("fundSourceCodes", "");
        nameValuePair[7] = new BasicNameValuePair("fullText", "");
        request.putExtra(BaseConstant.POST_NAME_VALUE_PAIR, nameValuePair);
        request.putExtra("yTotal", yTotal);
        request.putExtra("nTotal", nTotal);
        request.putExtra("x", x);
        Spider.create(new ChinaBiddingProcessor()).setDownloader(new DungProxyDownloader()).thread(1)
                .addRequest(request).run();
//      Map<String, Object> listMap = new HashMap<String, Object>();
//      listMap.put("zoneCode", zoneCodeList);
//      listMap.put("normIndustry", normIndustryList);
//      listMap.put("rangeType", rangeTypeList);
//      listMap.put("fullText", fullTextList);
//      listMap.put("fundSourceCodes", fundSourceCodeList);
//      NameValuePair[] nameValuePairs = new NameValuePair[8];
//      nameValuePairs[0] = new BasicNameValuePair("poClass", "BidResult");
//      nameValuePairs[1] = new BasicNameValuePair("infoClassCodes", "0108");
//      nameValuePairs[2] = new BasicNameValuePair("pubDate", "");
//      nameValuePairs[3] = new BasicNameValuePair("rangeType", "");
//      nameValuePairs[4] = new BasicNameValuePair("normIndustry", "");
//      nameValuePairs[5] = new BasicNameValuePair("zoneCode", "");
//      nameValuePairs[6] = new BasicNameValuePair("fundSourceCodes", "");
//      nameValuePairs[7] = new BasicNameValuePair("fullText", "");
//      String group =zf(nameValuePairs);
//      System.err.println(group);
//      NameValuePair[] nameValuePairs2=recf(nameValuePairs, listMap, groupList, "zoneCode");
//      System.err.println(JSON.toJSONString(nameValuePairs2));
    }
}
