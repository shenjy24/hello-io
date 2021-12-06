package com.jonas.simple;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * 聊天客户端
 *
 * @author shenjy 2019/01/06
 */
public class ChatClient {

    private Selector selector;
    private final Charset charset = StandardCharsets.UTF_8;

    public void start(String host, int port) {
        try {
            selector = Selector.open();
            InetSocketAddress address = new InetSocketAddress(host, port);
            //客户端SocketChannel
            SocketChannel socketChannel = SocketChannel.open(address);
            socketChannel.configureBlocking(false);
            socketChannel.register(selector, SelectionKey.OP_READ);

            //启动读取服务端数据的线程
            new ClientThread().start();

            //创建键盘输入流
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()) {
                if (socketChannel.isOpen()) {
                    //读取键盘输入
                    String line = scanner.nextLine();
                    //将键盘输入的内容输出到SocketChannel中
                    socketChannel.write(charset.encode(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientThread extends Thread {
        @Override
        public void run() {
            try {
                while (selector.select() > 0) {
                    for (SelectionKey selectionKey : selector.selectedKeys()) {
                        selector.selectedKeys().remove(selectionKey);
                        if (selectionKey.isReadable()) {
                            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(8);
                            StringBuilder content = new StringBuilder();

                            int readBytes = socketChannel.read(buffer);
                            if (-1 == readBytes) {
                                selectionKey.cancel();
                                socketChannel.close();
                            } else {
                                while (0 < readBytes) {
                                    buffer.flip();
                                    content.append(charset.decode(buffer));
                                    buffer.clear();

                                    readBytes = socketChannel.read(buffer);
                                    if (-1 == readBytes) {
                                        selectionKey.cancel();
                                        socketChannel.close();
                                        break;
                                    }
                                }
                                if (0 < content.length()) {
                                    System.out.println("聊天信息：" + content);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatClient().start("127.0.0.1", 30000);
    }
}
