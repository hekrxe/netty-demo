package com.hekrxe.netty.demo.apollo.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

import java.util.List;

/**
 * Created by tanhuayou on 2019/01/18
 */
final class NettyCodec extends ByteToMessageCodec<Pack> {
    @Override
    public void encode(ChannelHandlerContext ctx, Pack msg, ByteBuf out) {
        out.writeInt(msg.getMessage().length + 1);
        out.writeByte(msg.getType());
        out.writeBytes(msg.getMessage());
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        while (true) {
            int oldReadIndex = in.readerIndex();
            int readableBytes = in.readableBytes();
            if (readableBytes < 4) {
                break;
            }

            int length = in.readInt();
            if (length > 0 && length <= readableBytes) {
                byte type = in.readByte();
                byte[] bytes = null;

                if (length > 1) {
                    bytes = new byte[length - 1];
                    in.readBytes(bytes);
                }

                out.add(new Pack(type, bytes));
            } else {
                in.readerIndex(oldReadIndex);
                break;
            }
        }
    }
}
