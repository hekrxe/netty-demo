package com.hekrxe.netty.demo.io.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author tanhuayou on 2019/09/04
 */
@SuppressWarnings("ALL")
public class NioServer {


    public static void main(String[] args) throws Exception {
        //
        Selector selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // 设置为非阻塞
        serverChannel.configureBlocking(false);
        serverChannel.socket().bind(new InetSocketAddress(6767));

        // 将ServerChannel注册到选择器上 并注明感兴趣的事件 并给此channel添加一个附件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT, selector);

        for (; ; ) {
            // 阻塞 或 超时
            selector.select(1500);
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            if (!iterator.hasNext()) {
                System.out.println("Idle, run biz task...");
                continue;
            }

            while (iterator.hasNext()) {
                SelectionKey next = iterator.next();
                iterator.remove();
                handle(next);
            }
        }
    }

    private static void handle(SelectionKey key) throws Exception {
        if (key.isAcceptable()) {
            // 一定是ServerChannel,所以其附加对象是 selector
            Selector selector = (Selector) key.attachment();

            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            // 客户端连接上来了
            SocketChannel channel = serverChannel.accept();
            System.out.println("New Client Accepted: " + ((InetSocketAddress) channel.getRemoteAddress()).getPort());
            // non-block
            channel.configureBlocking(false);

            // 将该客户端也注册到selector上 注明此channel感兴趣的是OP_READ
            channel.register(selector, SelectionKey.OP_READ);
        } else if (key.isReadable()) {
            SocketChannel channel = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(32);

            // 如果通道处于阻塞模式，则此方法将阻塞直到至少读取一个字节
            int read;
            while ((read = channel.read(buffer)) > 0) {
                buffer.flip();
                while (buffer.hasRemaining()) {
                    System.out.print((char) buffer.get());
                }
                buffer.rewind();
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                buffer.clear();
            }
            if (-1 == read) {
                System.out.println("Client Close: " + ((InetSocketAddress) channel.getRemoteAddress()).getPort());
                channel.close();
                key.cancel();
            }
        } else {
            System.out.println("un catch key: " + key.interestOps());
        }
    }
}
