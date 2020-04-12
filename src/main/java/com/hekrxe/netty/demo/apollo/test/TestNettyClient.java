package com.hekrxe.netty.demo.apollo.test;


import com.hekrxe.netty.demo.apollo.Channel;
import com.hekrxe.netty.demo.apollo.ChannelHandler;
import com.hekrxe.netty.demo.apollo.Config;
import com.hekrxe.netty.demo.apollo.netty.client.NettyClient;

import java.net.InetSocketAddress;

/**
 * Created by tanhuayou on 2019/01/20
 */
public class TestNettyClient {
    public static void main(String[] args) throws Exception {
        ChannelHandler handler = new ChannelHandler() {
            @Override
            public void onMessage(Channel channel, byte[] message) {
                System.out.println("Client: " + new String(message));
            }

            @Override
            public void connected(Channel channel) {
                System.out.println("connected");
            }

            @Override
            public void disconnected(Channel channel) {
                System.out.println("disconnected");
            }

            /**
             * 心跳
             *
             * @param channel chl
             */
            @Override
            public void heartbeat(Channel channel) {
                System.out.println("heartbeat");
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
        config.setAddress(new InetSocketAddress(6767));
        config.setHandler(handler);
        config.setBossThreadNumber(2);
        NettyClient client = new NettyClient(config);
        client.connect();

        client.send("Hi".getBytes());
    }
}
