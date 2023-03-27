package com.jonas.example.order.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class InboundHandler2 extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf data = (ByteBuf) msg;
        System.out.println("InboundHandler2 channelRead 服务端收到数据：" + data.toString(CharsetUtil.UTF_8));

        // 执行的是ctx.channel().writeAndFlush，它会直接从tail开始往前找Outbound执行，
        // 链表中的顺序为head->in1->in2->out1->out2->tail，所以会执行out2，再执行out1
        ctx.channel().writeAndFlush(Unpooled.copiedBuffer("InboundHandler2 " + data.toString(CharsetUtil.UTF_8), CharsetUtil.UTF_8));
        // 执行的是ctx.writeAndFlush，它会从当前InboundHandler2的位置，往前找Outbound执行，
        // 根据链表中的顺序为head->in1->in2->out1->out2->tail，in2之前已经无Outbound，所以不会再有Outbound会执行
        // ctx.writeAndFlush(Unpooled.copiedBuffer("InboundHandler2 " + data.toString(CharsetUtil.UTF_8), CharsetUtil.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
