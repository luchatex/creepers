package com.fosun.fc.projects.creepers.downloader;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.fosun.fc.projects.creepers.constant.BaseConstant;
import com.fosun.fc.projects.creepers.schedule.RedisScheduler;
import com.fosun.fc.projects.creepers.utils.CommonMethodUtils;
import com.fosun.fc.projects.creepers.utils.JdbcUtil;
import com.google.common.collect.Sets;
import com.virjar.dungproxy.client.model.AvProxy;
import com.virjar.dungproxy.client.util.PoolUtil;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.utils.HttpConstant;

public class CreditChinaDungDownloader extends DungProxyDownloader {
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
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
            CloseableHttpClient httpClient = getHttpClient(site);
            httpResponse = httpClient.execute(httpUriRequest, httpClientContext);
            statusCode = httpResponse.getStatusLine().getStatusCode();
            request.putExtra(Request.STATUS_CODE, statusCode);
            AvProxy proxy = PoolUtil.getBindProxy(httpClientContext);
            if (proxy != null) {
                logger.info("==========>>> 当前使用代理ip:" + proxy.getIp() + ":" + proxy.getPort());
            }
            if (statusAccept(acceptStatCode, statusCode)) {
                    Page page = handleResponse(request, charset, httpResponse, task);
                    onSuccess(request);
                    page.putField(BaseConstant.HTTP_CLIENT_CONTEXT, httpClientContext);
                    page.putField(BaseConstant.PARAM_DTO_KEY, param);
                    logger.info("exit DungProxyDownloader download!");
                    return page;
            } else {
                logger.warn("code error " + statusCode + "\t" + request.getUrl());
                throw new IOException("code error " + statusCode + "\t" + request.getUrl());
            }
        } catch (IOException e) {
            logger.warn("catch IOException  and handle it! \t code error " + statusCode + "\n error:" + e);
            if (HttpStatus.SC_FORBIDDEN == statusCode || HttpStatus.SC_INTERNAL_SERVER_ERROR == statusCode|| HttpStatus.SC_BAD_REQUEST == statusCode) {
                logger.info("==========>>>  !!  offline当前使用代理ip!");
                PoolUtil.offline(httpClientContext);
            }
            e.printStackTrace();
            logger.warn("downloader error \t:" + request.getUrl());
            if (site.getCycleRetryTimes() > 0) {
                logger.info("exit DungProxyDownloader download!");
                return this.addToCycleRetry(request, site, httpClientContext);
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
    
    @Override
    protected Page addToCycleRetry(Request request, Site site ,HttpClientContext httpClientContext) {
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
                        map.put("t", String.valueOf(System.currentTimeMillis()));
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
