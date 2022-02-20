package com.jonas.codec.delimiterbased;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * TimeClientHandler
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-20
 */
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    private int counter;
    private static final String ECHO_REQ = "Hi, Jonas. Welcome to Netty.$_";

    public EchoClientHandler() {
    }

    /**
     * 当客户端和服务端TCP链路建立成功之后，Netty的NIO线程会调用channelActive方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 0; i < 10; i++) {
            ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String body = (String) msg;
        System.out.println("This is " + ++counter + " times receive server : [" + body + "]");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
