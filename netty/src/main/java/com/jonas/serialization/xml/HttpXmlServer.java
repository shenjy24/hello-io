package com.jonas.serialization.xml;

import com.jonas.serialization.xml.codec.HttpXmlResponseDecoder;
import com.jonas.serialization.xml.codec.HttpXmlResponseEncoder;
import com.jonas.serialization.xml.pojo.Order;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetSocketAddress;

/**
 * HttpXmlServer
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-04-04
 */
public class HttpXmlServer {

    public static void main(String[] args) throws Exception {
        HttpXmlServer server = new HttpXmlServer();
        server.run(8080);
    }

    public void run(int port) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("xml-decoder", new HttpXmlResponseDecoder(Order.class, true));
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            ch.pipeline().addLast("xml-encoder", new HttpXmlResponseEncoder());
                            ch.pipeline().addLast("xml-server-handler", new HttpXmlServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(new InetSocketAddress(port)).sync();
            System.out.println("HTTP 订购服务器启动，网址是 ： http://localhost:" + port);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
