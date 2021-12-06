package com.jonas.threadmodel.client;

import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ClientHandler
 *
 * @author shenjy
 * @version 1.0
 * @date 2021-01-18
 */
public class ClientHandler implements Runnable {

    private final SelectionKey selectionKey;
    private final SocketChannel socketChannel;

    private ByteBuffer readBuffer = ByteBuffer.allocate(8192);
    private ByteBuffer sendBuffer = ByteBuffer.allocate(2048);

    private final static int READ = 0;
    private final static int SEND = 1;

    private int status = READ;
    private AtomicInteger counter = new AtomicInteger();

    @SneakyThrows
    public ClientHandler(Selector selector, SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(false);
        this.selectionKey = socketChannel.register(selector, 0);
        //附加处理对象，当前是Handler对象，run是对象处理业务的方法
        selectionKey.attach(this);
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            switch (status) {
                case SEND:
                    send();
                    break;
                case READ:
                    read();
                    break;
                default:
            }
        } catch (IOException e) { //这里的异常处理是做了汇总，同样的，客户端也面临着正在与服务端进行写/读数据时，突然因为网络等原因，服务端直接断掉连接，这个时候客户端需要关闭自己并退出程序
            System.err.println("send或read时发生异常！异常信息：" + e.getMessage());
            selectionKey.cancel();
            try {
                socketChannel.close();
            } catch (IOException e2) {
                System.err.println("关闭通道时发生异常！异常信息：" + e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    void send() throws IOException {
        if (selectionKey.isValid()) {
            sendBuffer.clear();
            int count = counter.incrementAndGet();
            if (count <= 10) {
                sendBuffer.put(String.format("客户端发送的第%s条消息", count).getBytes());
                sendBuffer.flip(); //切换到读模式，用于让通道读到buffer里的数据
                socketChannel.write(sendBuffer);

                //则再次切换到读，用以接收服务端的响应
                status = READ;
                selectionKey.interestOps(SelectionKey.OP_READ);
            } else {
                selectionKey.cancel();
                socketChannel.close();
            }
        }
    }

    private void read() throws IOException {
        if (selectionKey.isValid()) {
            readBuffer.clear(); //切换成buffer的写模式，用于让通道将自己的内容写入到buffer里
            int readByte = socketChannel.read(readBuffer);
            if (0 < readByte) {
                System.out.println(String.format("收到来自服务端的消息: %s", new String(readBuffer.array())));
            }

            //收到服务端的响应后，再继续往服务端发送数据
            status = SEND;
            selectionKey.interestOps(SelectionKey.OP_WRITE); //注册写事件
        }
    }
}
