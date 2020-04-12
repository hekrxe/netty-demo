package com.hekrxe.netty.demo.apollo;

import java.net.SocketAddress;

/**
 * Created by tanhuayou on 2019/01/19
 */
public class Config {
    private ChannelHandler handler;
    private SocketAddress address;
    private int heartbeatSeconds;
    private int bossThreadNumber;
    private int workThreadNumber;

    public int getHeartbeatSeconds() {
        return heartbeatSeconds;
    }

    public Config setHeartbeatSeconds(int heartbeatSeconds) {
        this.heartbeatSeconds = heartbeatSeconds;
        return this;
    }

    public ChannelHandler getHandler() {
        return handler;
    }

    public Config setHandler(ChannelHandler handler) {
        this.handler = handler;
        return this;
    }

    public SocketAddress getAddress() {
        return address;
    }

    public Config setAddress(SocketAddress address) {
        this.address = address;
        return this;
    }

    public int getBossThreadNumber() {
        return bossThreadNumber;
    }

    public Config setBossThreadNumber(int bossThreadNumber) {
        this.bossThreadNumber = bossThreadNumber;
        return this;
    }

    public int getWorkThreadNumber() {
        return workThreadNumber;
    }

    public Config setWorkThreadNumber(int workThreadNumber) {
        this.workThreadNumber = workThreadNumber;
        return this;
    }
}
