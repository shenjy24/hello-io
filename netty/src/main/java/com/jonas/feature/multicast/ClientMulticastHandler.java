package com.jonas.feature.multicast;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.CharsetUtil;

/**
 * 组播客户端处理器
 *
 * @author shenjy
 * @version 1.0
 * @date 2022-02-20
 */
public class ClientMulticastHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
        String response = msg.content().toString(CharsetUtil.UTF_8);
        System.out.println("client receive message from server");
        if (response.startsWith("QOTM: ")) {
            System.out.println("Quote of the Moment: " + response.substring(6));
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
