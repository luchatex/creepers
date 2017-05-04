package com.fosun.fc.projects.creepers.downloader;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.UrlUtils;

/**
 * 天眼查
 *
 * @author MaXin <br>
 * @since 2017-1-11 17:19:45
 */
@Component("tianYanChaSeleniumDownloader")
public class TianYanChaSeleniumDownloader extends SeleniumDownloader {

    private volatile WebDriverPool webDriverPool;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @SuppressWarnings("unused")
    private int sleepTime = 0;

    private int poolSize = 1;

    private int maxWaitTime = 90;

    /**
     * 新建
     *
     * @param chromeDriverPath
     *            chromeDriverPath
     */
    public TianYanChaSeleniumDownloader(String chromeDriverPath) {
        System.getProperties().setProperty("webdriver.chrome.driver", chromeDriverPath);
    }

    /**
     * Constructor without any filed. Construct PhantomJS browser
     * 
     * @author bob.li.0718@gmail.com
     */
    public TianYanChaSeleniumDownloader() {
        System.getProperties().setProperty("webdriver.chrome.driver", CommonMethodUtils.getChromeWebDriver());
        System.getProperties().setProperty("phantomjs.binary.path", CommonMethodUtils.getPhantomJSWebDriver());
        System.getProperties().setProperty("webdriver.firefox.bin", CommonMethodUtils.getFirefoxWebDriver());
    }

    /**
     * set sleep time to wait until load success
     *
     * @param sleepTime
     *            sleepTime
     * @return this
     */
    public TianYanChaSeleniumDownloader setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Page download(Request request, Task task) {
        checkInit();
        WebDriver webDriver;
        try {
            webDriver = webDriverPool.get();
        } catch (InterruptedException e) {
            logger.warn("interrupted", e);
            return null;
        }
        logger.info("downloading page " + request.getUrl());
        webDriver.get(request.getUrl());
        WebDriver.Options manage = webDriver.manage();
        Site site = task.getSite();
        if (site.getCookies() != null) {
            for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
                Cookie cookie = new Cookie(cookieEntry.getKey(), cookieEntry.getValue());
                manage.addCookie(cookie);
            }
        }

        WebDriverWait wait = new WebDriverWait(webDriver, maxWaitTime);
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                // return isElementExsit(webDriver,
                // "//*[@class=\"search_result_single ng-scope\"]");
                return isElementExsit(webDriver, "//div[contains(.,\"没有找到相关结果\")]") || webDriver.findElement(By.xpath("//*[@class=\"search_result_single ng-scope\"]")).isDisplayed()
                        ;
            }
        });

        if (isElementExsit(webDriver, "//div[contains(.,\"没有找到相关结果\")]") ) {
            Page page = new Page();
            page.setRawText("没有找到相关结果");
            page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs("没有找到相关结果", request.getUrl())));
            page.setUrl(new PlainText(request.getUrl()));
            page.setRequest(request);
            page.putField(BaseConstant.PARAM_DTO_KEY, param);
            webDriverPool.returnToPool(webDriver);
            return page;
        }

        WebElement clickElement = webDriver.findElement(By.xpath("//div[@class=\"search_result_single ng-scope\"]/div[@class=\"row\"]//a"));
        clickElement.click();
        String current_handle = webDriver.getWindowHandle();
        Set<String> all_handles = webDriver.getWindowHandles();
        Iterator<String> it = all_handles.iterator();
        String handle = null;
        while (it.hasNext()) {
            handle = it.next();
            if (current_handle == handle)
                continue;
            webDriver.switchTo().window(handle);
        }

        wait = new WebDriverWait(webDriver, maxWaitTime);
        wait.until(new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                // return isElementExsit(webDriver,
                // "//*[@class=\"search_result_single ng-scope\"]");
                return webDriver.findElement(By.xpath("//*[@class=\"row b-c-white company-content\"]")).isDisplayed();
            }
        });

        WebElement webElement = webDriver.findElement(By.xpath("/html"));
        String content = webElement.getAttribute("outerHTML");
        webDriver.close();
        webDriver.switchTo().window(current_handle);
        Page page = new Page();
        page.setRawText(content);
        page.setHtml(new Html(UrlUtils.fixAllRelativeHrefs(content, request.getUrl())));
        page.setUrl(new PlainText(request.getUrl()));
        page.setRequest(request);
        page.putField(BaseConstant.PARAM_DTO_KEY, param);
        webDriverPool.returnToPool(webDriver);
        return page;
    }

    private void checkInit() {
        if (webDriverPool == null) {
            synchronized (this) {
                webDriverPool = new WebDriverPool(poolSize);
            }
        }
    }

    @Override
    public void setThread(int thread) {
        this.poolSize = thread;
    }

    @Override
    public void close() throws IOException {
        webDriverPool.closeAll();
    }
}
