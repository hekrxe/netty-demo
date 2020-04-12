package com.hekrxe.netty.demo.apollo;

/**
 * Created by tanhuayou on 2019/01/19
 */
public interface ChannelHandler {
    /**
     * 收到一条消息
     *
     * @param channel chl
     * @param message message
     */
    void onMessage(Channel channel, byte[] message);

    /**
     * 与此连接已激活
     *
     * @param channel chl
     */
    void connected(Channel channel);

    /**
     * 与此连接断开
     *
     * @param channel chl
     */
    void disconnected(Channel channel);

    /**
     * 心跳
     *
     * @param channel chl
     */
    void heartbeat(Channel channel);

    /**
     * 心跳超时
     * 此时此连接不可信(可能已被断开)
     *
     * @param channel chl
     */
    void heartbeatTimeout(Channel channel);

    /**
     * 异常产生
     *
     * @param channel chl
     * @param cause   cause
     */
    void exceptionCaught(Channel channel, Throwable cause);
}
