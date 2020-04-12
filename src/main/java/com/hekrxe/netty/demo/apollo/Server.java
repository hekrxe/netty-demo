package com.hekrxe.netty.demo.apollo;


import com.hekrxe.netty.demo.apollo.exception.TransportException;

/**
 * Created by tanhuayou on 2019/01/17
 */
public interface Server {
    void start() throws TransportException;

    boolean isClosed();

    void close();
}
