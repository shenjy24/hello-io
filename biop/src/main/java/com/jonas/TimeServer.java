package com.jonas;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 伪异步模式TimeServer
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
            /**
             * 线程池和消息队列都是有界的，无论客户端并发连接数多大，都不会导致线程个数过于膨胀或者内存溢出
             */
            TimeServerHandlerExecutorPool singleExecutor = new TimeServerHandlerExecutorPool(50, 10000);
            Socket socket = null;
            while (true) {
                socket = server.accept();
                singleExecutor.execute(new TimeServerHandler(socket));
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
