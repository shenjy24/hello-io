package com.jonas.example.order;

import com.jonas.example.order.handler.InboundHandler1;
import com.jonas.example.order.handler.InboundHandler2;
import com.jonas.example.order.handler.OutboundHandler1;
import com.jonas.example.order.handler.OutboundHandler2;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;


public class OrderServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new InboundHandler1());
        pipeline.addLast(new InboundHandler2());
        pipeline.addLast(new OutboundHandler1());
        pipeline.addLast(new OutboundHandler2());
    }

}
