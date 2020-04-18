package com.hekrxe.netty.demo.io.bio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author tanhuayou on 2019/09/04
 */
@SuppressWarnings("ALL")
public class BioServer {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(6767);
        while (true) {
            // 阻塞住直到一个连接的到来
            // 阻塞期间主线程啥也干不了
            Socket client = serverSocket.accept();
            System.out.println("New Client Accepted: " + client.getPort());
            // 一个线程管理一个连接上的所有读写操作
            // 存在的问题 连接数受制于系统能创建的线程数
            new Thread(() -> {
                try {
                    handle(client);
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }).start();
        }
    }

    private static void handle(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();
        try {
            byte[] bytes = new byte[1024];
            while (true) {
                //读取数据阻塞
                int read = inputStream.read(bytes);
                if (read != -1) {
                    System.out.print(new String(bytes, 0, read));
                    outputStream.write(bytes, 0, read);
                    outputStream.flush();
                } else {
                    break;
                }
            }
        } finally {
            try {
                System.out.println("Client Close: " + socket.getPort());
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            inputStream.close();
            outputStream.close();
        }
    }
}
