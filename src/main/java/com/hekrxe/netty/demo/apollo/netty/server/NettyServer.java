package com.hekrxe.netty.demo.apollo.netty.server;

import com.hekrxe.netty.demo.apollo.Config;
import com.hekrxe.netty.demo.apollo.Server;
import com.hekrxe.netty.demo.apollo.exception.TransportException;
import com.hekrxe.netty.demo.apollo.netty.HandlerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

/**
 * Created by tanhuayou on 2019/01/17
 */
public class NettyServer implements Server {
    private InternalLogger logger = InternalLoggerFactory.getInstance(NettyServer.class);

    private Config config;
    private EventLoopGroup boss;
    private EventLoopGroup work;
    private Channel bossChannel;
    private Class<? extends ServerSocketChannel> serverSocketChannel;

    private volatile boolean started = false;

    public NettyServer(Config config) {
        this.config = config;
        pick();
    }

    @Override
    public void start() throws TransportException {
        if (started) {
            logger.warn("Already Started");
            return;
        }
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, work)
                .channel(serverSocketChannel)
                .option(ChannelOption.SO_BACKLOG, 512)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new HandlerChannelInitializer(config));
        try {
            bossChannel = bootstrap.bind(config.getAddress()).syncUninterruptibly().channel();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new TransportException(e);
        }
        started = true;
        logger.info("NettyServer Started");
    }

    @Override
    public boolean isClosed() {
        return !started;
    }

    @Override
    public void close() {
        try {
            bossChannel.close().sync();
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
        try {
            boss.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
        try {
            work.shutdownGracefully().sync();
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
        }
        started = false;
    }

    private void pick() {
        DefaultThreadFactory bossFactory = new DefaultThreadFactory("NettyServer-Boss");
        DefaultThreadFactory workFactory = new DefaultThreadFactory("NettyServer-Work");
        if (Epoll.isAvailable()) {
            this.serverSocketChannel = EpollServerSocketChannel.class;
            boss = new EpollEventLoopGroup(config.getBossThreadNumber(), bossFactory);
            work = new EpollEventLoopGroup(config.getWorkThreadNumber(), workFactory);
        } else {
            this.serverSocketChannel = NioServerSocketChannel.class;
            boss = new NioEventLoopGroup(config.getBossThreadNumber(), bossFactory);
            work = new NioEventLoopGroup(config.getWorkThreadNumber(), workFactory);
        }
    }
}
