package com.jonas.threadmodel.multithread;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Acceptor模块负责处理连接就绪事件
 *
 * @author shenjy
 * @version 1.0
 * @date 2021-01-18
 */
public class ServerAcceptor implements Runnable {
    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    public ServerAcceptor(Selector selector, ServerSocketChannel serverSocketChannel) {
        this.selector = selector;
        this.serverSocketChannel = serverSocketChannel;
    }

    @Override
    public void run() {
        SocketChannel socketChannel;
        try {
            socketChannel = serverSocketChannel.accept();
            if (null != socketChannel) {
                System.out.printf("收到来自%s的连接%n", socketChannel.getRemoteAddress());
                //Handler负责接下来的事件处理（除了连接事件以外的事件均可）
                new ServerAsyncHandler(selector, socketChannel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
