package com.jonas.serialization.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * SubReqServerHandler
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-28
 */
public class SubReqServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        SubscribeReq req = (SubscribeReq) msg;
        if ("jonas".equalsIgnoreCase(req.getUserName())) {
            System.out.println("Server accept client subscribe req : [" + req.toString() + "]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }

    private SubscribeResp resp(int subReqID) {
        SubscribeResp resp = new SubscribeResp();
        resp.setSubReqID(subReqID);
        resp.setRespCode(0);
        resp.setDesc("Netty book order succeed, 3 days later, sent to the designated address");
        return resp;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
