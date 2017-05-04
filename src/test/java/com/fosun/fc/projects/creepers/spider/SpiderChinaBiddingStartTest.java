//package com.fosun.fc.projects.creepers.spider;
//
//import java.util.Date;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.fosun.fc.projects.creepers.constant.BaseConstant;
//import com.fosun.fc.projects.creepers.downloader.DungProxyDownloader;
//import com.fosun.fc.projects.creepers.pageprocessor.ChinaBiddingProcessor;
//
//import us.codecraft.webmagic.Request;
//import us.codecraft.webmagic.Spider;
//import us.codecraft.webmagic.utils.HttpConstant;
//
//public class SpiderChinaBiddingStartTest extends SpiderBaseTest {
//
//    @Autowired(required = true)
//    private ChinaBiddingProcessor chinaBiddingProcessor;
//
//    @Test
//    public void run() {
//        methodTest();
//    }
//
//    public void methodTest() {
//        String url = "http://www.chinabidding.com/search/proj.htm"+"?t="+new Date().getTime();
//        Request request = new Request(url);
//        request.setMethod(HttpConstant.Method.POST);
//        NameValuePair[] nameValuePair = new NameValuePair[8];
//        nameValuePair[0] = new BasicNameValuePair("poClass", "BidResult");
//        nameValuePair[1] = new BasicNameValuePair("infoClassCodes", "0108");
//        nameValuePair[2] = new BasicNameValuePair("pubDate", "");
//        nameValuePair[3] = new BasicNameValuePair("rangeType", "");
//        nameValuePair[4] = new BasicNameValuePair("normIndustry", "");
//        nameValuePair[5] = new BasicNameValuePair("zoneCode", "11*");
//        nameValuePair[6] = new BasicNameValuePair("fundSourceCodes", "");
//        nameValuePair[7] = new BasicNameValuePair("fullText", "");
//        request.putExtra(BaseConstant.POST_NAME_VALUE_PAIR, nameValuePair);
//        String url1 = "http://www.chinabidding.com/search/proj.htm"+"?t="+new Date().getTime();
//        Request request1 = new Request(url1);
//        request1.setMethod(HttpConstant.Method.POST);
//        NameValuePair[] nameValuePair1 = new NameValuePair[8];
//        nameValuePair1[0] = new BasicNameValuePair("poClass", "BidResult");
//        nameValuePair1[1] = new BasicNameValuePair("infoClassCodes", "0108");
//        nameValuePair1[2] = new BasicNameValuePair("pubDate", "");
//        nameValuePair1[3] = new BasicNameValuePair("rangeType", "");
//        nameValuePair1[4] = new BasicNameValuePair("normIndustry", "");
//        nameValuePair1[5] = new BasicNameValuePair("zoneCode", "11*");
//        nameValuePair1[6] = new BasicNameValuePair("fundSourceCodes", "");
//        nameValuePair1[7] = new BasicNameValuePair("fullText", "");
//        request1.putExtra(BaseConstant.POST_NAME_VALUE_PAIR, nameValuePair1);
//        Spider.create(chinaBiddingProcessor).setDownloader(new DungProxyDownloader()).thread(1)
//                .addRequest(request).addRequest(request1).run();
//    }
//}
