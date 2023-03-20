package com.jonas.aio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步IO实现的服务器
 */
public class ChatServer {

    private static List<AsynchronousSocketChannel> channelList = new ArrayList<>();

    public void start(int port) {
        try {
            //创建一个线程池
            ExecutorService executor = Executors.newFixedThreadPool(20);
            //以指定线程池来创建一个AsynchronousChannelGroup
            AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(executor);
            //以指定线程池来创建一个AsynchronousServerSocketChannel
            AsynchronousServerSocketChannel serverChannel =
                    AsynchronousServerSocketChannel.open(channelGroup).bind(new InetSocketAddress(port));
            //使用CompletionHandler接收来自客户端的连接请求(回调)，此处并不会发生阻塞
            serverChannel.accept(null, new AcceptHandler(serverChannel));
            // 主线程继续自己的行为
            Thread.sleep(5000);
            System.out.println("服务端启动成功");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * 处理客户端连接请求的回调处理器
     */
    private static class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {

        private final AsynchronousServerSocketChannel serverChannel;
        private final ByteBuffer buffer = ByteBuffer.allocate(1024);

        public AcceptHandler(AsynchronousServerSocketChannel serverChannel) {
            this.serverChannel = serverChannel;
        }

        /**
         * 当实际IO操作完成时触发该方法
         */
        @Override
        public void completed(AsynchronousSocketChannel sc, Object attachment) {
            //记录新连接进来的Channel
            channelList.add(sc);
            //准备接收客户端的下一次连接
            serverChannel.accept(null, this);

            sc.read(buffer, null, new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(Integer result, Object attachment) {
                    buffer.flip();
                    String content = StandardCharsets.UTF_8.decode(buffer).toString();
                    //遍历每个Channel, 将收到的信息写入各Channel中
                    for (AsynchronousSocketChannel c : channelList) {
                        try {
                            c.write(ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8))).get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    buffer.clear();
                    //读取下一次数据
                    sc.read(buffer, null, this);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("读取数据失败:" + exc);
                    //从该Channel中读取数据失败，将该Channel删除
                    channelList.remove(sc);
                }
            });
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            System.out.println("连接失败:" + exc);
        }
    }

    public static void main(String[] args) {
        new ChatServer().start(30000);
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
