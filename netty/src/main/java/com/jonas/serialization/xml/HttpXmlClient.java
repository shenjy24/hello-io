package com.jonas.serialization.xml;

import com.jonas.serialization.xml.codec.HttpXmlRequestEncoder;
import com.jonas.serialization.xml.codec.HttpXmlResponseDecoder;
import com.jonas.serialization.xml.pojo.Order;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

import java.net.InetSocketAddress;

/**
 * HttpXmlClient
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-04-03
 */
public class HttpXmlClient {

    public static void main(String[] args) throws Exception {
        HttpXmlClient client = new HttpXmlClient();
        client.connect(8080);
    }

    public void connect(int port) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //负责将二进制码流解码成为HTTP的应答消息
                            ch.pipeline().addLast("http-decoder", new HttpResponseDecoder());
                            //负责将1个HTTP请求消息的多个部分合并成一条完整的HTTP消息
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("xml-decoder", new HttpXmlResponseDecoder(Order.class, true));
                            ch.pipeline().addLast("http-encoder", new HttpRequestEncoder());
                            ch.pipeline().addLast("xml-encoder", new HttpXmlRequestEncoder());
                            ch.pipeline().addLast("xml-client-handler", new HttpXmlClientHandler());
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(port)).sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
