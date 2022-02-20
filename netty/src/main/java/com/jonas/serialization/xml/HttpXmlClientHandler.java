package com.jonas.serialization.xml;

import com.jonas.serialization.xml.codec.HttpXmlRequest;
import com.jonas.serialization.xml.codec.HttpXmlResponse;
import com.jonas.serialization.xml.pojo.OrderFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * HttpXmlClientHandler
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-04-03
 */
public class HttpXmlClientHandler extends SimpleChannelInboundHandler<HttpXmlResponse> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        HttpXmlRequest request = new HttpXmlRequest(null, OrderFactory.create(123));
        ctx.writeAndFlush(request);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpXmlResponse msg) throws Exception {
        System.out.println("The client receive response of http header is : " + msg.getResponse().headers().names());
        System.out.println("The client receive response of http body is : " + msg.getResult());
    }
}
