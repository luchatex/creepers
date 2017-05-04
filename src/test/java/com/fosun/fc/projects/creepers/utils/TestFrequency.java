package com.fosun.fc.projects.creepers.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.sym.Name;
import com.fosun.fc.projects.creepers.spider.SpiderBaseTest;

public class TestFrequency extends SpiderBaseTest {
    
    @Autowired
    private Frequency frequency;

    @Test
    public void testFrequency() {
        for(int i = 0;i<30;i++){
            ThreadFrequency t1 = new ThreadFrequency("t"+i);
        }
    }
    class ThreadFrequency extends Thread {
        String name = "";
        public ThreadFrequency(String threadName) {
            this.name = threadName;
        }
        public void frequency() {
            
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            @SuppressWarnings("rawtypes")
            Class[] classes = { String.class, String[].class, String[].class, String.class };
            String sourceMail = "zx.service@fosun.com";
            String[] targetMails = new String[] { "lizhanping@fosun.com" };
            String[] ccMails = new String[] { "pengyk@fosun.com", "maxin@fosun.com", "158869557@qq.com" };
            String ip="";
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            String content = "各位爬虫组的同事们：\n" + "      爬虫服务器出现bug，请尽快处理问题，以下是详细信息：\n" + "错误路径：" + "test" + "\n"
                    + "错误信息：" + "test" + "\n" + "请求信息：" + "test" + "\n" + "ip地址：" + ip;
            Object[] parameterValues = { sourceMail, targetMails, ccMails, content };
            frequency.frequency("com.fosun.fc.projects.creepers.service.impl.SimpleMailServiceImpl", "sendNotificationMail",
                classes, parameterValues, "mail.maxTimeInterval", "mail.maxTimes", "test",
                "test");
        }
    }
}

