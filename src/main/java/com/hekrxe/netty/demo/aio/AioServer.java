package com.hekrxe.netty.demo.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @author tanhuayou on 2019/09/04
 */
public class AioServer {
    public static void main(String[] args) throws IOException {
        CountDownLatch latch = new CountDownLatch(1);

        AsynchronousServerSocketChannel asyncServerChannel = AsynchronousServerSocketChannel.open();
        asyncServerChannel.bind(new InetSocketAddress(6767));
        Attach attach = new Attach(latch, asyncServerChannel);
        try {
            asyncServerChannel.accept(attach, new AcceptHandler());
            System.out.println("启动完成... 可以干其他事去了");
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
