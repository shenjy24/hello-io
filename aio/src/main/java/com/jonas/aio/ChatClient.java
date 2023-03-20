package com.jonas.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 异步IO实现的客户端
 */
public class ChatClient {
    //与服务器端通信的异步Channel
    private AsynchronousSocketChannel clientChannel;
    private final Charset charset = StandardCharsets.UTF_8;

    public void connect(String host, int port) {
        try {
            ExecutorService executor = Executors.newFixedThreadPool(80);
            AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(executor);
            clientChannel = AsynchronousSocketChannel.open(channelGroup);
            //阻塞线程，直到连接完成
            clientChannel.connect(new InetSocketAddress(host, port)).get();
            System.out.println("---与服务器连接成功---");

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            clientChannel.read(buffer, null, new CompletionHandler<Integer, Object>() {
                @Override
                public void completed(Integer result, Object attachment) {
                    buffer.flip();
                    String content = charset.decode(buffer).toString();
                    System.out.println("某人说:" + content);
                    buffer.clear();
                    clientChannel.read(buffer, null, this);
                }

                @Override
                public void failed(Throwable exc, Object attachment) {
                    System.out.println("读取数据失败: " + exc);
                }
            });

            //创建键盘输入流
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                //读取键盘输入
                String line = scanner.nextLine();
                //将键盘输入的内容输出到SocketChannel中
                clientChannel.write(charset.encode(line));
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        client.connect("127.0.0.1", 30000);
    }
}
