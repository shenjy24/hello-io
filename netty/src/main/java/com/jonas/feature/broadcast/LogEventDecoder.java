package com.jonas.feature.broadcast;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

/**
 * LogEventDecoder
 *
 * @author shenjy
 * @version 1.0
 * @date 2022-02-20
 */
public class LogEventDecoder extends MessageToMessageDecoder<DatagramPacket> {
    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket datagramPacket, List<Object> out) throws Exception {
        ByteBuf data = datagramPacket.content();
        int i = data.indexOf(0, data.readableBytes(), LogEvent.SEPARATOR);
        String fileName = data.slice(0, i).toString(CharsetUtil.UTF_8);
        String logMsg = data.slice(i + 1, data.readableBytes()).toString(CharsetUtil.UTF_8);
        LogEvent logEvent = new LogEvent(datagramPacket.recipient(), fileName, logMsg, System.currentTimeMillis());
        out.add(logEvent);
    }
}
