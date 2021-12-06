package com.jonas.threadmodel.singlethread;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Handler负责读写操作
 *
 * @author shenjy
 * @version 1.0
 * @date 2021-01-18
 */
public class ServerHandler implements Runnable {
    
    private final SelectionKey selectionKey;
    private final SocketChannel socketChannel;

    private ByteBuffer readBuffer = ByteBuffer.allocate(2048);
    private ByteBuffer sendBuffer = ByteBuffer.allocate(8192);
    
    private final static int READ = 0;
    private final static int SEND = 1;
    
    private int status = READ;

    public ServerHandler(Selector selector, SocketChannel socketChannel) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        selectionKey = socketChannel.register(selector, 0);
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
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
            sendBuffer.clear();
            sendBuffer.put(String.format("收到来自%s的信息: %s", socketChannel.getRemoteAddress(), new String(readBuffer.array())).getBytes());
            sendBuffer.flip();
            //write方法结束，意味着本次写就绪变为写完毕，标记着一次事件的结束
            int count = socketChannel.write(sendBuffer);
            if (count < 0) {
                //同上，write场景下，取到-1，也意味着客户端断开连接
                selectionKey.cancel();
                socketChannel.close();
                System.out.println("send时-------连接关闭");
            }

            //没断开连接，则再次切换到读
            status = READ;
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    private void read() throws IOException {
        if (selectionKey.isValid()) {
            readBuffer.clear();
            //read方法结束，意味着本次"读就绪"变为"读完毕"，标记着一次就绪事件的结束
            int count = socketChannel.read(readBuffer);
            if (count > 0) {
                System.out.println(String.format("收到来自%s的消息: %s", socketChannel.getRemoteAddress(), new String(readBuffer.array())));
                status = SEND;
                selectionKey.interestOps(SelectionKey.OP_WRITE); //注册写方法
            } else {
                //读模式下拿到的值是-1，说明客户端已经断开连接，那么将对应的selectKey从selector里清除，否则下次还会select到，因为断开连接意味着读就绪不会变成读完毕，也不cancel，下次select会不停收到该事件
                //所以在这种场景下，（服务器程序）你需要关闭socketChannel并且取消key，最好是退出当前函数。注意，这个时候服务端要是继续使用该socketChannel进行读操作的话，就会抛出“远程主机强迫关闭一个现有的连接”的IO异常。
                selectionKey.cancel();
                socketChannel.close();
                System.out.println("read时-------连接关闭");
            }
        }
    }
}
