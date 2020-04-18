package com.hekrxe.netty.demo.io.zero;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author tanhuayou on 2019/09/06
 */
@SuppressWarnings("InfiniteLoopStatement")
public class MMap {

    public static void main(String[] args) throws Exception {
        String home = System.getProperty("user.home");
        RandomAccessFile accessFile = new RandomAccessFile(new File(home + "/zsh_install.sh"), "rw");

        FileChannel srcChannel = accessFile.getChannel();

        // 这是一个只读的 DirectByteBuffer,叫 DirectByteBufferR.
        // TODO: 两次上下文切换 mmap
        MappedByteBuffer mappedByteBuffer = srcChannel.map(FileChannel.MapMode.READ_ONLY, 0, srcChannel.size());


        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 6767));
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        ByteBuffer recvBuf = ByteBuffer.allocate(1024);

        while (true) {
            selector.select(1500);
            Set<SelectionKey> keys = selector.selectedKeys();
            if (keys.isEmpty()) {
                System.out.println("Run Biz Task...");
                continue;
            }

            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                SocketChannel channel = (SocketChannel) key.channel();
                if (key.isConnectable()) {
                    if (channel.finishConnect()) {
                        channel.register(selector, SelectionKey.OP_READ);
                        // TODO: CPU copy
                        // TODO: 两次上下文切换
                        channel.write(mappedByteBuffer);

                        srcChannel.close();
                    }
                } else if (key.isReadable()) {
                    recvBuf.clear();
                    channel.read(recvBuf);
                    recvBuf.flip();
                    byte[] bytes = new byte[recvBuf.remaining()];
                    recvBuf.get(bytes);
                    System.out.print(new String(bytes));
                }
                iterator.remove();
            }
        }

    }
}
