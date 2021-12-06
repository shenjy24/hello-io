package com.jonas.threadmodel.client;

import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Client
 *
 * @author shenjy
 * @version 1.0
 * @date 2021-01-18
 */
public class ClientReactor implements Runnable {

    private final Selector selector;
    private final SocketChannel socketChannel;

    @SneakyThrows
    public ClientReactor(String ip, int port) {
        selector = Selector.open();
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress(ip, port));
        SelectionKey sk = socketChannel.register(selector, SelectionKey.OP_CONNECT);
        sk.attach(new ClientConnector(selector, socketChannel));
    }

    @SneakyThrows
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for (SelectionKey selectionKey : selectionKeys) {
                dispatch(selectionKey);
            }
            selectionKeys.clear();
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        Runnable task = (Runnable) selectionKey.attachment();
        if (null != task) {
            task.run();
        }
    }
}
