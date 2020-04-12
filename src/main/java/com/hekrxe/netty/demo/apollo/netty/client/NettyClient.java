package com.hekrxe.netty.demo.apollo.netty.client;

import com.hekrxe.netty.demo.apollo.Client;
import com.hekrxe.netty.demo.apollo.Config;
import com.hekrxe.netty.demo.apollo.exception.TransportException;
import com.hekrxe.netty.demo.apollo.netty.HandlerChannelInitializer;
import com.hekrxe.netty.demo.apollo.netty.NettyChannel;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Created by tanhuayou on 2019/01/17
 */
public class NettyClient implements Client {
    private InternalLogger logger = InternalLoggerFactory.getInstance(getClass());

    private Config config;
    private final NioEventLoopGroup group;

    private volatile Channel channel;
    private volatile boolean connected = false;


    public NettyClient(Config config) {
        this.config = config;
        group = new NioEventLoopGroup(config.getBossThreadNumber(), new DefaultThreadFactory("NettyClient"));
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public synchronized void reconnect() throws TransportException {
        close();
        connect();
    }

    @Override
    public synchronized void connect() throws TransportException {
        if (connected) {
            logger.warn("Already connected " + channel);
            return;
        }
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .option(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new HandlerChannelInitializer(config));
        ChannelFuture future = bootstrap.connect(config.getAddress());
        try {
            boolean connected = future.awaitUninterruptibly(5000, TimeUnit.MILLISECONDS);
            if (connected && future.isSuccess()) {
                channel = future.channel();
            } else {
                close();
                throw new TransportException("Connected failed," + config.getAddress());
            }
        } catch (Exception e) {
            throw new TransportException(e);
        }
        connected = true;
    }

    @Override
    public boolean send(byte[] message) throws TransportException {
        if (!connected || null == channel || !channel.isActive()) {
            connect();
        }
        if (null != channel && channel.isActive()) {
            NettyChannel.getOrAddChannel(channel).send(message);
        }
        return true;
    }

    @Override
    public synchronized void close() {
        if (null != channel) {
            channel.closeFuture().awaitUninterruptibly();
        }
        group.shutdownGracefully().awaitUninterruptibly();
        connected = false;
    }
}
