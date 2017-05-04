/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.fosun.fc.projects.creepers.web.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.bouncycastle.i18n.filter.HTMLFilter;
import org.hyperic.sigar.Sigar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.fosun.fc.projects.creepers.utils.SystemInfoUtil;

@ServerEndpoint(value = "/monitor")
public class PerformanceMonitor {

    private static Logger log = LoggerFactory.getLogger(PerformanceMonitor.class);
    private static final String PM_PREFIX = "pm";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<PerformanceMonitor> connections = new CopyOnWriteArraySet<>();

    private final String nickname;
    private Session session;
    private static Timer timer = new Timer();

    public PerformanceMonitor() {
        nickname = PM_PREFIX + connectionIds.getAndIncrement();
    }

    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        log.info("add user:" + this.nickname);
        if(connections.size()==1){
            log.info("Timer open!");
            try {
                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        Map<String, Object> map=SystemInfoUtil.usage(new Sigar());
                        String result = JSON.toJSONString(map);
                        broadcast(result);
                    }
                }, 1000, 1000);
            } catch (Exception e) {
//                e.printStackTrace();
                log.info("this timer happened error,new a timer again");
                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    public void run() {
                        Map<String, Object> map=SystemInfoUtil.usage(new Sigar());
                        String result = JSON.toJSONString(map);
                        broadcast(result);
                    }
                }, 1000, 1000);
            }
        }
    }

    @OnClose
    public void end() {
        connections.remove(this);
        log.info("@OnClose remove user:" + this.nickname);
        // 一旦集合中不存在用户，就关闭定时器
        if (connections.size() <= 0) {
            timer.cancel();
            log.info("@OnClose Timer cancel!");
        }
    }

    @OnMessage
    public void incoming(String message) {
        // Never trust the client
        HTMLFilter filter = new HTMLFilter();
        String filteredMessage = String.format("%s: %s", nickname, filter.doFilter(message.toString()));
        broadcast(filteredMessage);
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        log.error("the connection happened error,this name is " + this.nickname);
    }

    public static void broadcast(String msg) {
        for (PerformanceMonitor client : connections) {
            try {
                synchronized (client) {
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (IOException e) {
                log.debug("Chat Error: Failed to send message to client", e);
                connections.remove(client);
                // 一旦集合中不存在用户，就关闭定时器
                if (connections.size() <= 0) {
                    timer.cancel();
                    log.info("@OnError Timer cancel!");
                }
                try {
                    client.session.close();
                } catch (IOException e1) {
                    // Ignore
                }
                String message = String.format("* %s %s", client.nickname, "has been disconnected.");
                broadcast(message);
            }
        }
    }
}
