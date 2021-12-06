package com.jonas.threadmodel.singlethread;

import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Set;

/**
 * Reactor模块负责监听就绪事件和对事件的分发处理
 *
 * @author shenjy
 * @version 1.0
 * @date 2021-01-18
 */
public class ServerReactor implements Runnable {

    private final Selector selector;
    private final ServerSocketChannel serverSocketChannel;

    @SneakyThrows
    public ServerReactor(int port) {
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        SelectionKey sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        //attach callback object
        sk.attach(new ServerAcceptor(selector, serverSocketChannel));
    }

    @SneakyThrows
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            for (SelectionKey selectionKey : selectionKeys) {
                //任务分发
                dispatch(selectionKey);
            }
            selectionKeys.clear();
        }
    }

    void dispatch(SelectionKey selectionKey) {
        Runnable task = (Runnable) selectionKey.attachment();
        //调用之前注册的callback对象
        if (null != task) {
            //此处直接调用run方法，因此并不是启动新线程
            task.run();
        }
    }
}
