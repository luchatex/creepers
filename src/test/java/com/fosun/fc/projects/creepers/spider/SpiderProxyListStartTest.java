package com.fosun.fc.projects.creepers.spider;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fosun.fc.projects.creepers.downloader.HttpRequestDownloader;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.pageprocessor.proxy.ProxyMimvpProcessor;
import com.fosun.fc.projects.creepers.pipeline.ProxyMimvpPipline;

import us.codecraft.webmagic.Spider;

public class SpiderProxyListStartTest extends SpiderBaseTest {

    @Autowired(required = true)
    private ProxyMimvpProcessor proxyMimvpProcessor;

    @Autowired(required = true)
    private ProxyMimvpPipline proxyMimvpPipline;

    public void methodTest() {
        CreepersParamDTO param = new CreepersParamDTO();
        Spider.create(proxyMimvpProcessor).addPipeline(proxyMimvpPipline).setDownloader(new HttpRequestDownloader().setParam(param))
                .addUrl("http://proxy.mimvp.com/free.php?proxy=in_tp")
                .addUrl("http://proxy.mimvp.com/free.php?proxy=in_hp").thread(2).runAsync();
    }

    @Test
    public void run() {
         methodTest();
    }

}
