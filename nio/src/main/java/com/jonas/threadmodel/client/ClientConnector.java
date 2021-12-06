package com.jonas.threadmodel.client;

import lombok.SneakyThrows;

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * ClientConnector
 *
 * @author shenjy
 * @version 1.0
 * @date 2021-01-18
 */
public class ClientConnector implements Runnable {

    private final Selector selector;
    private final SocketChannel socketChannel;

    public ClientConnector(Selector selector, SocketChannel socketChannel) {
        this.selector = selector;
        this.socketChannel = socketChannel;
    }

    @SneakyThrows
    @Override
    public void run() {
        //连接完成
        if (socketChannel.finishConnect()) {
            System.out.println(String.format("已完成%s的连接", socketChannel.getRemoteAddress()));
            new ClientHandler(selector, socketChannel);
        }
    }
}
