package com.hekrxe.netty.demo.io.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author tanhuayou on 2019/09/04
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Attach> {

    @Override
    public void completed(AsynchronousSocketChannel channel, Attach attachment) {
        attachment.getAsyncServerChannel().accept(attachment, this);

        try {
            System.out.println("New Client Accepted: " + ((InetSocketAddress) channel.getRemoteAddress()).getPort());
        } catch (IOException ignored) {
        }
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // 异步读取数据 数据copy完成后 由 ReadHandler处理
        channel.read(buffer, buffer, new ReadHandler(channel));
    }


    @Override
    public void failed(Throwable exc, Attach attachment) {
        exc.printStackTrace();
    }
}
