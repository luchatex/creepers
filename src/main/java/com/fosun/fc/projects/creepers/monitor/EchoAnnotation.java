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
package com.fosun.fc.projects.creepers.monitor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.PongMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The three annotated echo endpoints can be used to test with Autobahn and the
 * following command "wstest -m fuzzingclient -s servers.json". See the Autobahn
 * documentation for setup and general information.
 */
@ServerEndpoint("/wsecho")
public class EchoAnnotation {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final String ID_PREFIX = "id";
    // private static final AtomicInteger connectionIds = new AtomicInteger(0);
    private static final Set<EchoAnnotation> connections = new CopyOnWriteArraySet<EchoAnnotation>();

    private final String id;
    private Session session;

    public EchoAnnotation() {
        id = ID_PREFIX + UUID.randomUUID();
    }

    @OnOpen
    public void start(Session session) {
        this.session = session;
        connections.add(this);
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(id);
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    @OnMessage
    public void echoTextMessage(Session session, String msg, boolean last) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendText(msg, last);
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    @OnMessage
    public void echoBinaryMessage(Session session, ByteBuffer bb,
            boolean last) {
        try {
            if (session.isOpen()) {
                session.getBasicRemote().sendBinary(bb, last);
            }
        } catch (IOException e) {
            try {
                session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }

    /**
     * Process a received pong. This is a NO-OP.
     *
     * @param pm    Ignored.
     */
    @OnMessage
    public void echoPongMessage(PongMessage pm) {
        // NO-OP
    }

    @OnClose
    public void end() {
        connections.remove(this);
        String message = String.format("* %s %s", id, "has disconnected.");
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            try {
                this.session.close();
            } catch (IOException e1) {
                // Ignore
            }
        }
    }
    
    @OnError
    public void error(Throwable t) throws Throwable {
        connections.remove(this);
        logger.error("EchoAnnotation happened Error ");
    }
    
    public static void echo(String id,String msg){
        for(EchoAnnotation echoAnnotation:connections){
            if(echoAnnotation.id.equals(id)){
                try {
                    echoAnnotation.session.getBasicRemote().sendText(msg);
                } catch (IOException e) {
                    try {
                        echoAnnotation.session.close();
                    } catch (IOException e1) {
                        // Ignore
                    }
                }
            }
        }
    }
}
