package com.jonas.example.chat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends ChannelInboundHandlerAdapter {

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 当从服务端收到新的客户端连接时，客户端的 Channel 存入 ChannelGroup 列表中，并通知列表中的其他客户端 Channel
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        // Broadcast a message to multiple Channels
        channels.writeAndFlush("[Server] - " + incoming.remoteAddress() + " 加入\n");
        channels.add(incoming);
    }

    /**
     * 当从服务端收到客户端断开时，客户端的 Channel 自动从 ChannelGroup 列表中移除了，并通知列表中的其他客户端 Channel
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        channels.writeAndFlush("[Server] - " + incoming.remoteAddress() + " 离开\n");
    }

    /**
     * 当从服务端读到客户端写入信息时，将信息转发给其他客户端的 Channel
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel incoming = ctx.channel();
        for (Channel channel : channels) {
            if (incoming != channel) {
                channel.writeAndFlush("[" + incoming.remoteAddress() + "]:" + msg + "\n");
            } else {
                channel.writeAndFlush("[you]:" + msg + "\n");
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("ChatClient:" + incoming.remoteAddress() + "在线\n");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("ChatClient:" + incoming.remoteAddress() + "掉线\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("ChatClient:" + incoming.remoteAddress() + "异常\n");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
