package com.jonas.threadmodel.masterslave;

import java.io.IOException;
import java.nio.channels.SelectionKey;
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
    private final ServerSocketChannel serverSocketChannel;
    private final int coreNum = Runtime.getRuntime().availableProcessors(); // 获取CPU核心数
    private final Selector[] selectors = new Selector[coreNum];
    private ServerSubReactor[] serverSubReactors = new ServerSubReactor[coreNum];

    private int next = 0; // 轮询使用subReactor的下标索引
    private final Thread[] threads = new Thread[coreNum]; // subReactor的处理线程

    public ServerAcceptor(ServerSocketChannel serverSocketChannel) throws IOException {
        this.serverSocketChannel = serverSocketChannel;
        // 初始化
        for (int i = 0; i < coreNum; i++) {
            selectors[i] = Selector.open();
            serverSubReactors[i] = new ServerSubReactor(selectors[i], i); //初始化sub reactor
            threads[i] = new Thread(serverSubReactors[i]); //初始化运行sub reactor的线程
            threads[i].start(); //启动（启动后的执行参考SubReactor里的run方法）
        }
    }

    @Override
    public void run() {
        SocketChannel socketChannel;
        try {
            socketChannel = serverSocketChannel.accept(); // 连接
            if (null != socketChannel) {
                System.out.printf("收到来自%s的连接%n", socketChannel.getRemoteAddress());
                socketChannel.configureBlocking(false);
                serverSubReactors[next].registering(true); // 注意一个selector在select时是无法注册新事件的，因此这里要先暂停下select方法触发的程序段，下面的wakeup和这里的setRestart都是做这个事情的，具体参考SubReactor里的run方法
                selectors[next].wakeup(); // 使一個阻塞住的selector操作立即返回
                SelectionKey selectionKey = socketChannel.register(selectors[next], SelectionKey.OP_READ); // 当前客户端通道SocketChannel向selector[next]注册一个读事件，返回key
                selectors[next].wakeup(); // 使一個阻塞住的selector操作立即返回
                serverSubReactors[next].registering(false); // 本次事件注册完成后，需要再次触发select的执行，因此这里Restart要在设置回false（具体参考SubReactor里的run方法）
                selectionKey.attach(new ServerAsyncHandler(selectors[next], socketChannel)); // 绑定Handler
                if (++next == selectors.length) {
                    next = 0; //越界后重新分配
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
