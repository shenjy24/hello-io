package com.jonas.feature.broadcast;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.net.InetSocketAddress;

/**
 * 组播客户端，监听服务端发来的数据
 *
 * @author shenjy
 * @version 1.0
 * @date 2022-02-20
 */
public class LogEventMonitor {
    private final Bootstrap bootstrap;
    private final EventLoopGroup group;

    public LogEventMonitor(InetSocketAddress address) {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.group)
                .channel(NioDatagramChannel.class)
                .localAddress(address)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new LogEventDecoder());
                        pipeline.addLast(new LogEventHandler());
                    }
                });
    }

    public Channel bind() {
        return bootstrap.bind().syncUninterruptibly().channel();
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(LogEventEnv.BROADCAST_PORT);
        LogEventMonitor monitor = new LogEventMonitor(address);
        try {
            Channel channel = monitor.bind();
            System.out.println("LogEventMonitor running");
            channel.closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            monitor.stop();
        }
    }
}
