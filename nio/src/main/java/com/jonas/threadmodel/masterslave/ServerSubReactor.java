package com.jonas.threadmodel.masterslave;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

/**
 * ServerReactor负责处理读写操作
 *
 * @author shenjy
 * @version 1.0
 * @date 2021-01-22
 */
public class ServerSubReactor implements Runnable {

    private final Selector selector;
    private boolean register = false; //注册开关
    private int num; //序号，也就是Acceptor初始化SubReactor时的下标

    public ServerSubReactor(Selector selector, int num) {
        this.selector = selector;
        this.num = num;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.printf("%d号SubReactor等待注册中%n", num);
                while (!Thread.interrupted() && !register) {
                    if (0 < selector.select()) {
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> it = selectionKeys.iterator();
                        while (it.hasNext()) {
                            dispatch(it.next());
                            it.remove();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void dispatch(SelectionKey selectionKey) {
        Runnable task = (Runnable) selectionKey.attachment();
        if (null != task) {
            task.run();
        }
    }

    public void registering(boolean register) {
        this.register = register;
    }
}
