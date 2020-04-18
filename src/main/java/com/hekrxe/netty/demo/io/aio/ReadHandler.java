package com.hekrxe.netty.demo.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author tanhuayou on 2019/09/04
 */
public class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel channel;

    public ReadHandler(AsynchronousSocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void completed(Integer result, ByteBuffer buffer) {
        buffer.flip();
        int remaining = buffer.remaining();
        if (remaining <= 0) {
            try {
                System.out.println("Client Close: " + ((InetSocketAddress) channel.getRemoteAddress()).getPort());
            } catch (IOException ignored) {
            }
            return;
        }
        byte[] bytes = new byte[remaining];
        buffer.get(bytes);
        String message = new String(bytes);
        System.out.print(message);

        ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
        writeBuffer.put(bytes);
        writeBuffer.flip();

        // 异步写数据
        channel.write(writeBuffer, writeBuffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buf) {
                if (buf.hasRemaining()) {
                    channel.write(buf, buf, this);
                } else {
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    channel.read(readBuffer, readBuffer, new ReadHandler(channel));
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
            }
        });
    }

    @Override
    public void failed(Throwable exc, ByteBuffer buffer) {
    }
}
