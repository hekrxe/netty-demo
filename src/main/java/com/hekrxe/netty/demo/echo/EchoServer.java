package com.hekrxe.netty.demo.echo;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

/**
 * @author tanhuayou on 2020/4/12
 */
public class EchoServer {
    public static void main(String[] args) throws Exception {
        EchoServerHandler handler = new EchoServerHandler();
        NioEventLoopGroup boss = new NioEventLoopGroup(2, new DefaultThreadFactory("boss"));
        NioEventLoopGroup work = new NioEventLoopGroup(64, new DefaultThreadFactory("work"));
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(handler);
                    }
                })
                .bind(6767)
                .sync();
    }
}
