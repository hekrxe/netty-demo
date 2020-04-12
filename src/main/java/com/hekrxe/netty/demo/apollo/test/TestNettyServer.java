package com.hekrxe.netty.demo.apollo.test;

import com.hekrxe.netty.demo.apollo.Channel;
import com.hekrxe.netty.demo.apollo.ChannelHandler;
import com.hekrxe.netty.demo.apollo.Config;
import com.hekrxe.netty.demo.apollo.exception.TransportException;
import com.hekrxe.netty.demo.apollo.netty.server.NettyServer;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Created by tanhuayou on 2019/01/20
 */
public class TestNettyServer {
    public static void main(String[] args) throws TransportException {
        ChannelHandler handler = new ChannelHandler() {
            @Override
            public void onMessage(Channel channel, byte[] message) {
                System.out.println("Received: " + new String(message));
            }

            @Override
            public void connected(Channel channel) {
                System.out.println(channel.getRemoteAddress() +"\t connected");
                try {
                    channel.send("welcome!".getBytes(StandardCharsets.UTF_8));
                } catch (TransportException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void disconnected(Channel channel) {
                System.out.println(channel.getRemoteAddress() +"\t disconnected");
            }

            /**
             * 心跳
             *
             * @param channel chl
             */
            @Override
            public void heartbeat(Channel channel) {
                System.out.println("Heartbeat");
            }

            /**
             * 心跳超时
             * 此时此连接不可信(可能已被断开)
             *
             * @param channel chl
             */
            @Override
            public void heartbeatTimeout(Channel channel) {
                System.out.println("heartbeatTimeout");
            }

            @Override
            public void exceptionCaught(Channel channel, Throwable cause) {
                cause.printStackTrace();
            }
        };
        Config config = new Config();
        config.setBossThreadNumber(2)
                .setWorkThreadNumber(4)
                .setHandler(handler)
                .setHeartbeatSeconds(2)
                .setAddress(new InetSocketAddress(6767));
        new NettyServer(config).start();
    }
}
