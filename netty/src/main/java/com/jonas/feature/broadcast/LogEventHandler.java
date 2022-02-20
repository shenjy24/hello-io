package com.jonas.feature.broadcast;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * LogEventHandler
 *
 * @author shenjy
 * @version 1.0
 * @date 2022-02-20
 */
public class LogEventHandler extends SimpleChannelInboundHandler<LogEvent> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LogEvent msg) throws Exception {
        StringBuilder builder = new StringBuilder();
        builder.append(msg.getReceived()).append(" [").append(msg.getSource())
                .append("] [").append(msg.getLogfile()).append("] : ").append(msg.getMsg());
        System.out.println(builder);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
