package com.jonas.example.order;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * 测试Netty中的Handler执行顺序
 */
public class OrderServer {
    public void bind(int port) {
        /**
         * NioEventLoopGroup包含一组NIO线程，专门用于网络事件的处理，实际上就是Reactor线程组。
         * bossGroup用于服务端接受客户端的连接
         * workerGroup用于进行SocketChannel的网络读写
         */
        //配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //ServerBootstrap用于启动NIO服务端的辅助启动类
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置group和childGroup
            bootstrap.group(bossGroup, workerGroup)
                    //设置channelFactory
                    .channel(NioServerSocketChannel.class)
                    //设置参数
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //设置childHandler
                    .childHandler(new OrderServerChannelInitializer());
            //绑定端口，同步等待成功
            ChannelFuture future = bootstrap.bind(port).sync();
            //等待服务端监听端口关闭
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new OrderServer().bind(8080);
    }
}
