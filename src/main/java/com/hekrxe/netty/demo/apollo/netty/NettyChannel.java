package com.hekrxe.netty.demo.apollo.netty;


import com.hekrxe.netty.demo.apollo.exception.TransportException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by tanhuayou on 2019/01/18
 */
public class NettyChannel implements com.hekrxe.netty.demo.apollo.Channel  {
    private static final Map<Channel, NettyChannel> ALL_CHANNEL = new ConcurrentHashMap<>();

    private static final String SEND_TIMEOUT_MS = "@_SEND_TIMEOUT_MS_";

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();

    private final Channel channel;

    private NettyChannel(Channel channel) {
        this.channel = channel;
    }

    /**
     * get remote address.
     *
     * @return remote address.
     */
    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.remoteAddress();
    }

    /**
     * send message.
     *
     * @param message message
     */
    @Override
    public void send(byte[] message) throws TransportException {
        try {
            ChannelFuture future = channel.writeAndFlush(new Pack(message));
            Object timeout = getAttribute(SEND_TIMEOUT_MS);
            if (timeout instanceof Number) {
                future.await(((Number) timeout).longValue(), TimeUnit.MILLISECONDS);
            }
            if (null != future.cause()) {
                throw future.cause();
            }
        } catch (Throwable e) {
            throw new TransportException(e);
        }
    }

    /**
     * is connected.
     *
     * @return connected
     */
    @Override
    public boolean isConnected() {
        return channel.isActive();
    }

    /**
     * has attribute.
     *
     * @param key key.
     * @return has or has not.
     */
    @Override
    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    /**
     * get attribute.
     *
     * @param key key.
     * @return value.
     */
    @Override
    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    /**
     * set attribute.
     *
     * @param key   key.
     * @param value value.
     */
    @Override
    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    /**
     * remove attribute.
     *
     * @param key key.
     */
    @Override
    public void removeAttribute(String key) {
        attributes.remove(key);
    }

    static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isActive()) {
            ALL_CHANNEL.remove(ch);
        }
    }


    public static NettyChannel getOrAddChannel(Channel ch) {
        if (ch == null) {
            return null;
        }
        NettyChannel ret = ALL_CHANNEL.get(ch);
        if (ret == null) {
            NettyChannel nettyChannel = new NettyChannel(ch);
            if (ch.isActive()) {
                ALL_CHANNEL.putIfAbsent(ch, nettyChannel);
            }
            return ALL_CHANNEL.get(ch);
        }
        return ret;
    }
}
