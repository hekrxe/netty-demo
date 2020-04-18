package com.hekrxe.netty.demo.io.zero;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author tanhuayou on 2019/09/06
 */
@SuppressWarnings("ALL")
public class Sendfile {
    public static void main(String[] args) throws Exception {
        String home = System.getProperty("user.home");
        FileChannel srcChannel = new FileInputStream(new File(home + "/zsh_install.sh")).getChannel();

        Selector selector = Selector.open();

        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 6767));
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        ByteBuffer recvBuf = ByteBuffer.allocate(1024);
        while (true) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                SocketChannel socketChl = (SocketChannel) key.channel();
                if (key.isConnectable()) {
                    if (socketChl.finishConnect()) {
                        socketChl.register(selector, SelectionKey.OP_READ);

                        srcChannel.transferTo(0, srcChannel.size(), socketChl);

                        srcChannel.close();
                    }
                } else if (key.isReadable()) {
                    recvBuf.clear();
                    socketChl.read(recvBuf);
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
