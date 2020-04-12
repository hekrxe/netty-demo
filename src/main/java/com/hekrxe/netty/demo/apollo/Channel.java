package com.hekrxe.netty.demo.apollo;


import com.hekrxe.netty.demo.apollo.exception.TransportException;

import java.net.InetSocketAddress;

/**
 * Created by tanhuayou on 2019/01/18
 */
public interface Channel {
    /**
     * get remote address.
     *
     * @return remote address.
     */
    InetSocketAddress getRemoteAddress();

    /**
     * send message.
     *
     * @param message message
     */
    void send(byte[] message) throws TransportException;

    /**
     * is connected.
     *
     * @return connected
     */
    boolean isConnected();

    /**
     * has attribute.
     *
     * @param key key.
     * @return has or has not.
     */
    boolean hasAttribute(String key);

    /**
     * get attribute.
     *
     * @param key key.
     * @return value.
     */
    Object getAttribute(String key);

    /**
     * set attribute.
     *
     * @param key   key.
     * @param value value.
     */
    void setAttribute(String key, Object value);

    /**
     * remove attribute.
     *
     * @param key key.
     */
    void removeAttribute(String key);

}
