package com.hekrxe.netty.demo.apollo.netty;


import com.hekrxe.netty.demo.apollo.Config;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Created by tanhuayou on 2019/01/19
 */
public class HandlerChannelInitializer extends ChannelInitializer<Channel> {
    private Config config;

    public HandlerChannelInitializer(Config config) {
        this.config = config;
    }

    @Override
    protected void initChannel(Channel ch) {
        ch.pipeline()
                .addLast(new IdleStateHandler(0, 0, config.getHeartbeatSeconds()))
                .addLast("NettyCodec", new NettyCodec())
                .addLast("ClientHandler", new NettyInboundHandler(config.getHandler()))
        ;
    }
}
