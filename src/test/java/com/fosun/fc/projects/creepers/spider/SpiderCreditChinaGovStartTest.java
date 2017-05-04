package com.fosun.fc.projects.creepers.spider;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.fosun.fc.modules.test.category.UnStable;
import com.fosun.fc.modules.test.spring.SpringTransactionalTestCase;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.utils.Frequency;

@Category(UnStable.class)
@DirtiesContext
@ContextConfiguration(locations = { "/applicationContext.xml", "/redis/applicationContext-redis.xml","/schedule/applicationContext-spring-scheduler.xml" })
@TransactionConfiguration(defaultRollback = false)
public class SpiderCreditChinaGovStartTest extends SpringTransactionalTestCase {
    
    @Autowired
    private Frequency frequency;
    
    @Test
    public void run() {
//        creepersShixinServiceImpl.processByMerName("深圳市快播科技有限公司");
        @SuppressWarnings("rawtypes")
        Class[] classes = {String.class,String[].class,String[].class,String.class};
        String sourceMail = "zx.service@fosun.com";
        String[] targetMails = new String[] { "lizhanping@fosun.com" };
        String[] ccMails = new String[] { "pengyk@fosun.com", "maxin@fosun.com", "158869557@qq.com" };
        CreepersParamDTO param = new CreepersParamDTO();
        String content = "各位爬虫组的同事们：\n" + "      爬虫服务器出现bug，请尽快处理问题，以下是详细信息：\n" + "错误路径：" + param.getErrorPath() + "\n"
                + "错误信息：" + param.getErrorInfo() + "\n" + "请求信息：" + param.getBreakDownRequest();
        Object[] parameterValues = {sourceMail,targetMails,ccMails,content};
        for(int i =0;i<10;i++){
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            frequency.frequency("com.fosun.fc.projects.creepers.service.impl.SimpleMailServiceImpl", "sendNotificationMail", classes, parameterValues, "mail.maxTimeInterval", "mail.maxTimes", "false", i+"");
        }
    }

}
