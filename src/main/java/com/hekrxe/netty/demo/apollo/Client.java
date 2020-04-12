package com.hekrxe.netty.demo.apollo;


import com.hekrxe.netty.demo.apollo.exception.TransportException;

/**
 * Created by tanhuayou on 2019/01/17
 */
public interface Client {

    boolean send(byte[] message) throws TransportException;

    boolean isConnected();

    void reconnect() throws TransportException;

    void connect() throws TransportException;

    void close();
}
