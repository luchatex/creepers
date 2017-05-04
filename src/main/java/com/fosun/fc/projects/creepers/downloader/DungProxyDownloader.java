package com.fosun.fc.projects.creepers.downloader;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.dto.CreepersParamDTO;
import com.fosun.fc.projects.creepers.schedule.RedisScheduler;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.FileUtil;
import com.fosun.fc.projects.creepers.utils.JdbcUtil;
import com.google.common.collect.Sets;
import com.virjar.dungproxy.client.model.AvProxy;
import com.virjar.dungproxy.client.util.PoolUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.utils.HttpConstant;

/**
 * 为webMagic实现的downloader,如果有其他定制需求,参考本类实现即可<br/>
 * 
 * <pre>
 * public static void main(String[] args) {
 *     Spider.create(new GithubRepoPageProcessor()).addUrl("https://github.com/code4craft")
 *             .setDownloader(new DungProxyDownloader()).thread(5).run();
 * }
 * </pre>
 * 
 * <pre>
 *     如果自己实现代理池到httpclient的织入:
 *    CloseableHttpClient closeableHttpClient =
 *          HttpClientBuilder.create().setRetryHandler(new DunProxyHttpRequestRetryHandler())
 *          .setRoutePlanner(new ProxyBindRoutPlanner()).build();
 * </pre>
 *
 * Created by virjar on 16/10/30.
 */
@SuppressWarnings("deprecation")
@Component("dungProxyDownloader")
@Scope("prototype")
public class DungProxyDownloader extends HttpClientDownloader {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected CreepersParamDTO param = new CreepersParamDTO();

    private final Map<String, CloseableHttpClient> httpClients = new HashMap<String, CloseableHttpClient>();

    private DungProxyHttpClientGenerator httpClientGenerator = new DungProxyHttpClientGenerator();

    @Override
    protected HttpUriRequest getHttpUriRequest(Request request, Site site, Map<String, String> headers) {
        RequestBuilder requestBuilder = selectRequestMethod(request).setUri(request.getUrl());
        if (headers != null) {
            for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectionRequestTimeout(site.getTimeOut()).setSocketTimeout(site.getTimeOut())
                .setConnectTimeout(site.getTimeOut()).setCookieSpec(CookieSpecs.BEST_MATCH);
        if (site.getHttpProxyPool() != null && site.getHttpProxyPool().isEnable()) {
            HttpHost host = site.getHttpProxyFromPool();
            requestConfigBuilder.setProxy(host);
            request.putExtra(Request.PROXY, host);
        } else if (site.getHttpProxy() != null) {
            HttpHost host = site.getHttpProxy();
            requestConfigBuilder.setProxy(host);
            request.putExtra(Request.PROXY, host);
        }
        requestBuilder.setConfig(requestConfigBuilder.build());
        requestBuilder.setCharset(Consts.UTF_8);
        return requestBuilder.build();
    }

    @Override
    public Page download(Request request, Task task) {
        logger.info("entry DungProxyDownloader download!");
        param.setErrorPath(getClass().toString());
        Site site = null;
        if (task != null) {
            site = task.getSite();
        }
        Set<Integer> acceptStatCode;
        String charset = null;
        Map<String, String> headers = null;
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
            charset = site.getCharset();
            headers = site.getHeaders();
        } else {
            acceptStatCode = Sets.newHashSet(200);
        }
        logger.info("downloading page {}", request.getUrl());
        CloseableHttpResponse httpResponse = null;
        int statusCode = 0;
        HttpClientContext httpClientContext = HttpClientContext.adapt(new BasicHttpContext());
        try {
            HttpUriRequest httpUriRequest = getHttpUriRequest(request, site, headers);
            httpUriRequest.addHeader("User-Agent", CommonMethodUtils.getRandomUserAgent());
            CloseableHttpClient httpClient = getHttpClient(site);
            httpResponse = httpClient.execute(httpUriRequest, httpClientContext);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);
            AvProxy proxy = PoolUtil.getBindProxy(httpClientContext);
            if (proxy != null) {
                logger.info("==========>>> 当前使用代理ip:" + proxy.getIp() + ":" + proxy.getPort());
            }
            if (statusAccept(acceptStatCode, statusCode)) {
                if (request.getUrl().matches(param.getOrderUrl(BaseConstant.OrderUrlKey.DOWNLOAD_FILE_URL_1_REGEX))) {
                    String url = param.getOrderUrl(BaseConstant.OrderUrlKey.DOWNLOAD_FILE_URL_1_REGEX);
                    String fileInputStream = FileUtil.createRandomUUIDFileName(url.substring(url.lastIndexOf(".")));
                    FileUtils.copyInputStreamToFile(httpResponse.getEntity().getContent(), new File(fileInputStream));
                    logger.info("create download file:" + fileInputStream);
                    Page page = new Page();
                    page.setUrl(new PlainText(request.getUrl()));
                    page.setRequest(request);
                    onSuccess(request);
                    page.putField(BaseConstant.HTTP_CLIENT_CONTEXT, httpClientContext);
                    page.putField(BaseConstant.PARAM_DTO_KEY, param);
                    page.putField(BaseConstant.PAGE_FILE_PATH, fileInputStream);
                    logger.info("exit DungProxyDownloader download!");
                    return page;
                } else {
                    Page page = handleResponse(request, charset, httpResponse, task);
                    onSuccess(request);
                    page.putField(BaseConstant.HTTP_CLIENT_CONTEXT, httpClientContext);
                    page.putField(BaseConstant.PARAM_DTO_KEY, param);
                    logger.info("exit DungProxyDownloader download!");
                    return page;
                }
            } else {
                logger.warn("code error " + statusCode + "\t" + request.getUrl());
                throw new IOException("code error " + statusCode + "\t" + request.getUrl());
            }
        } catch (IOException e) {
            logger.warn("catch IOException  and handle it! \t code error " + statusCode + "\n error:" + e);
            if (HttpStatus.SC_FORBIDDEN == statusCode || HttpStatus.SC_INTERNAL_SERVER_ERROR == statusCode|| HttpStatus.SC_BAD_REQUEST == statusCode ||HttpStatus.SC_SERVICE_UNAVAILABLE == statusCode) {
                logger.info("==========>>>  !!  offline当前使用代理ip!");
                PoolUtil.offline(httpClientContext);
            }
            e.printStackTrace();
            logger.warn("downloader error \t:" + request.getUrl());
            if (site.getCycleRetryTimes() > 0) {
                logger.info("exit DungProxyDownloader download!");
                return addToCycleRetry(request, site,httpClientContext);
            }
            onError(request);
            logger.info("exit DungProxyDownloader download!");
            return null;
        } finally {
            request.putExtra(Request.STATUS_CODE, statusCode);
            try {
                if (httpResponse != null) {
                    // ensure the connection is released back to pool
                    EntityUtils.consume(httpResponse.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
                e.printStackTrace();
                logger.warn("close response fail \t:" + request.getUrl());
            }
        }
    }

    protected CloseableHttpClient getHttpClient(Site site) {
        if (site == null) {
            return httpClientGenerator.getClient(null);
        }
        String domain = site.getDomain();
        CloseableHttpClient httpClient = httpClients.get(domain);
        if (httpClient == null) {
            synchronized (this) {
                httpClient = httpClients.get(domain);
                if (httpClient == null) {
                    httpClient = httpClientGenerator.getClient(site);
                    httpClients.put(domain, httpClient);
                }
            }
        }
        return httpClient;
    }

    public CreepersParamDTO getParam() {
        return param;
    }

    public DungProxyDownloader setParam(CreepersParamDTO param) {
        this.param = param;
        return this;
    }

    protected Page addToCycleRetry(Request request, Site site,HttpClientContext httpClientContext) {
        Page page = new Page();
        page.putField(BaseConstant.PARAM_DTO_KEY, param);
        page.putField(BaseConstant.HTTP_CLIENT_CONTEXT, httpClientContext);
        Object cycleTriedTimesObject = request.getExtra(Request.CYCLE_TRIED_TIMES);
        if (cycleTriedTimesObject == null) {
            page.addTargetRequest(request.setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, 1));
        } else {
            int cycleTriedTimes = (Integer) cycleTriedTimesObject;
            cycleTriedTimes++;
            if (cycleTriedTimes >= site.getCycleRetryTimes()) {
                Request nextRequest = new RedisScheduler().pop(param.getTaskType());
                if (nextRequest != null) {
                    String updateQuery = "update t_creepers_task_list t set t.flag = '2' where t.url = '" + nextRequest.getUrl() + "'";
                    try {
                        JdbcUtil.execute(updateQuery);
                    } catch (ClassNotFoundException e) {
                        logger.error("JdbcUtil.execute  ClassNotFoundException!");
                        e.printStackTrace();
                    } catch (SQLException e) {
                        logger.error("JdbcUtil.execute  SQLException!");
                        e.printStackTrace();
                    }
                    if (HttpConstant.Method.POST.equals(nextRequest.getMethod())) {
                        String stringNameValuePair = (String) nextRequest.getExtra(BaseConstant.POST_NAME_VALUE_PAIR_STRING);
                        HashMap<String,String> map = JSON.parseObject(stringNameValuePair, new TypeReference<HashMap<String,String>>() {});
                        nextRequest = CommonMethodUtils.buildDefaultRequest(map,nextRequest);
                    }
                    page.addTargetRequest(nextRequest);
                    page.setNeedCycleRetry(true);
                    return page;
                }
            }
            page.addTargetRequest(request.setPriority(0).putExtra(Request.CYCLE_TRIED_TIMES, cycleTriedTimes));
        }
        page.setNeedCycleRetry(true);
        return page;
    }
}
