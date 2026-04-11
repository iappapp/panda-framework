/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.conn.HttpClientConnectionManager
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.github.iappapp.panda.common.http.config;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.http.conn.HttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpConnectionMonitor extends Thread {
    private static final Logger log = LoggerFactory.getLogger(HttpConnectionMonitor.class);
    private volatile boolean shutdown;
    private final List<HttpClientConnectionManager> connectionManagers = Collections.synchronizedList(new LinkedList());
    private int idleTimeout;

    HttpConnectionMonitor(int idleTimeout) {
        this.setDaemon(true);
        this.setName("httpclient-connection-monitor");
        this.idleTimeout = idleTimeout;
    }

    void addConnectionManager(HttpClientConnectionManager connectionManager) {
        this.connectionManagers.add(connectionManager);
    }

    @Override
    public void run() {
        try {
            while (!this.shutdown) {
                HttpConnectionMonitor httpConnectionMonitor = this;
                synchronized (httpConnectionMonitor) {
                    this.wait(2000L);
                    for (HttpClientConnectionManager connectionManager : this.connectionManagers) {
                        connectionManager.closeExpiredConnections();
                        connectionManager.closeIdleConnections((long)this.idleTimeout, TimeUnit.MILLISECONDS);
                    }
                }
            }
        }
        catch (InterruptedException ex) {
            log.error("http idle connect manager thread exception, shutdown the thread.");
            this.shutdown();
        }
    }

    private void shutdown() {
        this.shutdown = true;
        this.connectionManagers.clear();
        HttpConnectionMonitor httpConnectionMonitor = this;
        synchronized (httpConnectionMonitor) {
            this.notifyAll();
        }
    }
}

