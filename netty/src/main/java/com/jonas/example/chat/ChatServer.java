package com.jonas.example.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 聊天室服务端
 */
public class ChatServer {

    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            //bossGroup接收到连接，就会把连接信息注册到workerGroup上
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //ServerBootStrap.option parameters apply to the server socket (Server channel) that is listening for connections
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //ServerBootStrap.childOption applies to to a channel's channelConfig which gets created once the serverChannel accepts a client connection
                    //ServerBootStrap.childOption parameters apply to the socket that gets created once the connection is accepted by the server socket
                    //即childOption()作用于客户端连接时服务端创建的socket
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast("handler", new ChatServerHandler());
                            System.out.println("ChatClient:" + ch.remoteAddress() + "连接上");
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            System.out.println("ChatServer 关闭了");
        }
    }

    public static void main(String[] args) {
        new ChatServer().start(8080);
    }
}
