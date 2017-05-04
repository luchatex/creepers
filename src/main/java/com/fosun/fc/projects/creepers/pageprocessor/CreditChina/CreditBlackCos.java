package com.fosun.fc.projects.creepers.pageprocessor.CreditChina;

import org.slf4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.fosun.fc.projects.creepers.downloader.HttpRequestDownloader;
import com.fosun.fc.projects.creepers.pageprocessor.BaseTaskListProcessor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author Administrator
 *
 */
public class CreditBlackCos extends BaseTaskListProcessor implements PageProcessor {

	private Site site;
	final String NAME = "INAME";
	final String CODE = "CARDNUMBER";
	private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

	@Override
	public void process(Page page) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = JSONArray.parseArray(page.getJson().toString());
		for(int i=0; i<jsonArray.size();i++){
			page.putField(jsonArray.getJSONObject(i).get(NAME).toString(), jsonArray.getJSONObject(i).get(CODE));
		}
		logger.debug("");
	}

	@Override
	public Site getSite() {
		if (null == site) {
			site = Site.me().setDomain("www.creditchina.gov.cn")
					.setUserAgent(
							"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36")
					.setRetryTimes(5).setCycleRetryTimes(1).setSleepTime(7000).setTimeOut(30000).setCharset("UTF-8")
					.addHeader("Origin", "http://www.creditchina.gov.cn");
		}
		return site;
	}

	public static void main(String args[]) {
		String url = "http://www.creditchina.gov.cn/uploads/creditinfo/2";
		Spider.create(new CreditBlackCos()).addUrl(url).setDownloader(new HttpRequestDownloader()).addPipeline(new JsonFilePipeline("D:\\webmagic\\")).thread(1).run();
	}

}
