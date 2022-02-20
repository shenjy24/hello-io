package com.jonas.feature.multicast;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.CharsetUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;

/**
 * 组播客户端
 * 参考文档：https://colobu.com/2014/10/21/udp-and-unicast-multicast-broadcast-anycast/#Netty%E4%B8%8E%E5%8D%95%E6%92%AD%EF%BC%8C%E7%BB%84%E6%92%AD
 *
 * @author shenjy
 * @version 1.0
 * @date 2022-02-20
 */
public class MulticastClient {
    private final String localHost;
    private final InetSocketAddress groupAddress;

    public MulticastClient(String localHost, InetSocketAddress groupAddress) {
        this.localHost = localHost;
        this.groupAddress = groupAddress;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            InetAddress localAddress = InetAddress.getByName(localHost);
            if (localAddress == null) {
                System.out.println("配置的ip不存在: " + localAddress.getHostAddress());
                return;
            }
            NetworkInterface byInetAddress = NetworkInterface.getByInetAddress(localAddress);

            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channelFactory(new ChannelFactory<NioDatagramChannel>() {
                        public NioDatagramChannel newChannel() {
                            return new NioDatagramChannel(InternetProtocolFamily.IPv4);
                        }
                    })
                    .localAddress(localAddress, groupAddress.getPort())
                    //对应IP参数IP_MULTICAST_IF2，同IP_MULTICAST_IF但支持IPV6。
                    .option(ChannelOption.IP_MULTICAST_IF, byInetAddress)
                    //对应IP参数IP_MULTICAST_IF，设置对应地址的网卡为多播模式
                    .option(ChannelOption.IP_MULTICAST_ADDR, localAddress)
                    //复用端口
                    .option(ChannelOption.SO_REUSEADDR, true)
                    //设置读缓冲区为1MB
                    .option(ChannelOption.SO_RCVBUF, 1024 * 1024)
                    //设置写缓冲区为1MB
                    .option(ChannelOption.SO_SNDBUF, 1024 * 1024)
                    //对应IP参数IP_MULTICAST_LOOP，设置本地回环接口的组播功能。由于IP_MULTICAST_LOOP返回True表示关闭，所以Netty加上后缀_DISABLED防止歧义。
//                    .option(ChannelOption.IP_MULTICAST_LOOP_DISABLED, false)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        public void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientMulticastHandler());
                        }
                    });

            Channel ch = b.bind().sync().channel();
            //DatagramPacket指定组播地址，即进行组播消息
            ch.writeAndFlush(new DatagramPacket(
                    Unpooled.copiedBuffer("QOTM?", CharsetUtil.UTF_8),
                    groupAddress)).sync();

            ch.closeFuture().await();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        InetSocketAddress groupAddress = new InetSocketAddress(MulticastEnv.MULTICAST_HOST, MulticastEnv.MULTICAST_PORT);
        new MulticastClient("192.168.1.100", groupAddress).start();
    }
}
