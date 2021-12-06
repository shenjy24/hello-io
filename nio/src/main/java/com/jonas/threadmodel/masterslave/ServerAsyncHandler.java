package com.jonas.threadmodel.masterslave;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handler负责读写操作
 *
 * @author shenjy
 * @version 1.0
 * @date 2021-01-18
 */
public class ServerAsyncHandler implements Runnable {

    private final Selector selector;
    private final SelectionKey selectionKey;
    private final SocketChannel socketChannel;

    private ByteBuffer readBuffer = ByteBuffer.allocate(2048);
    private ByteBuffer sendBuffer = ByteBuffer.allocate(8192);
    
    private final static int READ = 0;
    private final static int SEND = 1;
    private final static int PROCESSING = 2;
    
    private int status = READ;

    //开启线程数为5的异步处理线程池
    private static final ExecutorService workers = Executors.newFixedThreadPool(5);

    public ServerAsyncHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        selectionKey = socketChannel.register(selector, 0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        this.selector = selector;
        this.selector.wakeup();
    }
    
    @Override
    public void run() {
        try {
            switch (status) {
                case READ:
                    read();
                    break;
                case SEND:
                    send();
                    break;
                default:
            }
        } catch (IOException e) {
            System.err.println("read或send时发生异常！异常信息:" + e.getMessage());
            selectionKey.cancel();
            try {
                socketChannel.close();
            } catch (IOException e2) {
                System.err.println("关闭通道时发生异常！异常信息:" + e.getMessage());
                e2.printStackTrace();
            }
        }
    }

    private void send() throws IOException {
        if (selectionKey.isValid()) {
            status = PROCESSING;
            workers.execute(this::sendWorker);
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    private void read() throws IOException {
        if (selectionKey.isValid()) {
            readBuffer.clear();
            //read方法结束，意味着本次"读就绪"变为"读完毕"，标记着一次就绪事件的结束
            int count = socketChannel.read(readBuffer);
            if (count > 0) {
                status = PROCESSING;
                workers.execute(this::readWorker); //异步处理
            } else {
                selectionKey.cancel();
                socketChannel.close();
                System.out.println("read时-------连接关闭");
            }
        }
    }

    //读入消息后的业务处理
    private void readWorker() {
        //模拟系统瓶颈
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.printf("收到客户端的消息:%s\n", new String(readBuffer.array()));
        status = SEND;
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    private void sendWorker() {
        try {
            sendBuffer.clear();
            sendBuffer.put(String.format("收到来自%s的信息:%s", socketChannel.getRemoteAddress(), new String(readBuffer.array())).getBytes());
            sendBuffer.flip();

            int count = socketChannel.write(sendBuffer);
            if (count < 0) {
                selectionKey.cancel();
                socketChannel.close();
                System.out.println("send时-------连接关闭");
            } else {
                status = READ;
            }
        } catch (IOException e) {
            System.err.println("异步处理send业务时发生异常！异常信息：" + e.getMessage());
            selectionKey.cancel();
            try {
                socketChannel.close();
            } catch (IOException e1) {
                System.err.println("异步处理send业务关闭通道时发生异常！异常信息：" + e.getMessage());
            }
        }
    }
}
