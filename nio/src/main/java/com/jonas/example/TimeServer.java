package com.jonas.example;

/**
 * NIO模式TimeServer
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-15
 */
public class TimeServer {

    public static void main(String[] args) {
        TimeServer timeServer = new TimeServer();
        timeServer.startServer(8080);
    }

    public void startServer(int port) {
        //创建Reactor线程，创建多路复用器并启动线程
        MultiplexerTimeServer timeServer = new MultiplexerTimeServer(port);
        new Thread(timeServer, "NIO-MultiplexerTimeServer-001").start();
    }
}
