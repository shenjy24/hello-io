package com.jonas.feature.broadcast;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.internal.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;

/**
 * 广播服务端
 *
 * @author shenjy
 * @version 1.0
 * @date 2022-02-20
 */
public class LogEventBroadcaster {
    private final Bootstrap bootstrap;
    private final File file;
    private final EventLoopGroup group;
    private Channel channel;

    public LogEventBroadcaster(InetSocketAddress broadcastAddress, File file) {
        this.group = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap();
        this.bootstrap.group(this.group)
                .channel(NioDatagramChannel.class)
                .option(ChannelOption.SO_BROADCAST, true)
                .handler(new LogEventEncoder(broadcastAddress));

        this.file = file;
    }

    public void bind(int port) {
        this.channel = bootstrap.bind(port).syncUninterruptibly().channel();
        System.out.println("LogEventBroadcaster running");
    }

    public void broadcast() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        String line;
        while ((line = raf.readLine()) != null) {
            if (!StringUtil.isNullOrEmpty(line)) {
                channel.writeAndFlush(new LogEvent(null, file.getAbsolutePath(), line, -1));
            }
        }
        raf.close();
    }

    public void stop() {
        group.shutdownGracefully();
    }

    public static void main(String[] args) {
        InetSocketAddress address = new InetSocketAddress(LogEventEnv.BROADCAST_HOST, LogEventEnv.BROADCAST_PORT);
        File file = new File(LogEventBroadcaster.class.getClassLoader().getResource("tomcat/web.properties").getPath());
        LogEventBroadcaster broadcaster = new LogEventBroadcaster(address, file);
        try {
            broadcaster.bind(8081);
            broadcaster.broadcast();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            broadcaster.stop();
        }
    }
}
