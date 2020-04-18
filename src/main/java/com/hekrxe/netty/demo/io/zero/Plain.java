package com.hekrxe.netty.demo.io.zero;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @author tanhuayou on 2019/09/06
 */
public class Plain {
    public static void main(String[] args) throws Exception {

        String home = System.getProperty("user.home");

        FileInputStream fileInputStream = (new FileInputStream(new File(home + "/zsh_install.sh")));

        Socket socket = new Socket("127.0.0.1", 6767);
        OutputStream outputStream = socket.getOutputStream();


        byte[] bytes = new byte[1024];
        int len;
        // TODO 两次上下文切换 一次copy
        while ((len = fileInputStream.read(bytes)) != -1) {
            // TODO 两次上下文切换 一次copy
            outputStream.write(bytes, 0, len);
        }

        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();

        while ((len = inputStream.read(bytes)) != -1) {
            System.out.print(new String(bytes, 0, len, StandardCharsets.UTF_8));
        }

        inputStream.close();
        outputStream.close();
        socket.close();
    }
}
