package com.jonas;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO服务端
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-15
 */
public class TimeServer {

    public static void main(String[] args) {
        new TimeServer().startServer(8080);
    }

    public void startServer(int port) {
        ServerSocket server = null;
        try {
            server = new ServerSocket(port);
            System.out.println("The time server is start in port : " + port);
            Socket socket = null;
            while (true) {
                //服务端主线程阻塞在accept，当有客户端接入时，就会执行后续代码
                socket = server.accept();
                //客户端连接到来时，新建线程执行客户端请求，此处可优化为线程池
                new Thread(new TimeServerHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != server) {
                System.out.println("The time server close");
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
