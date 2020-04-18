package com.hekrxe.netty.demo.io.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * 用来验证阻塞非阻塞
 *
 * @author tanhuayou on 2019/09/09
 */
public class NioClient {
    public static void main(String[] args) throws IOException {

        SocketChannel socketChannel = SocketChannel.open();
        // 阻塞
        socketChannel.configureBlocking(true);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 6767));

        ByteBuffer buffer = ByteBuffer.allocate(64);
        buffer.put("Hello!\n".getBytes());
        buffer.flip();

        socketChannel.write(buffer);
        buffer.clear();

        long t1 = System.currentTimeMillis();
        System.out.println("Write Done. " + t1);
        socketChannel.read(buffer);
        System.out.println("Read Done. " + (System.currentTimeMillis() - t1));
        buffer.flip();

        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        System.out.println(new String(bytes));
    }
}
