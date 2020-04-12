package com.hekrxe.netty.demo.apollo.netty;


import com.hekrxe.netty.demo.apollo.ChannelHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * Created by tanhuayou on 2019/01/18
 */
@io.netty.channel.ChannelHandler.Sharable
final class NettyInboundHandler extends ChannelInboundHandlerAdapter {
    private final static byte[] PING = new byte[0];
    private final ChannelHandler handler;

    NettyInboundHandler(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel());
        try {
            handler.connected(channel);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel());
        try {
            handler.disconnected(channel);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel());
        try {
            if (msg instanceof Pack) {
                Pack pack = (Pack) msg;
                if (Pack.Type.NORMAL == pack.getType()) {
                    handler.onMessage(channel, pack.getMessage());
                } else if (Pack.Type.SYS_IDLE == pack.getType()) {
                    handler.heartbeat(channel);
                } else {
                    super.channelRead(ctx, msg);
                }
            } else {
                super.channelRead(ctx, msg);
            }
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel());
        try {
            if (evt instanceof IdleStateEvent) {
                ctx.writeAndFlush(new Pack(Pack.Type.SYS_IDLE, PING))
                        .addListener((ChannelFutureListener) future -> {
                            if (!future.isSuccess()) {
                                // 在指定的时间内如果没有数据的读写操作
                                // 或者发一次ping失败
                                // 那么极有可能此连接已经坏掉了
                                // 那么关闭此连接
                                future.channel().close();
                                handler.heartbeatTimeout(channel);
                            }
                        });
            } else {
                super.userEventTriggered(ctx, evt);
            }
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        NettyChannel channel = NettyChannel.getOrAddChannel(ctx.channel());
        try {
            handler.exceptionCaught(channel, cause);
        } finally {
            NettyChannel.removeChannelIfDisconnected(ctx.channel());
        }
    }
}
