package com.jonas.serialization.protobuf;

import com.jonas.serialization.protobuf.proto.SubscribeReqProto;
import com.jonas.serialization.protobuf.proto.SubscribeRespProto;
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
        SubscribeReqProto.SubscribeReq req = (SubscribeReqProto.SubscribeReq) msg;
        if ("jonas".equalsIgnoreCase(req.getUserName())) {
            System.out.println("Server accept client subscribe req : [" + req.toString() + "]");
            ctx.writeAndFlush(resp(req.getSubReqID()));
        }
    }

    private SubscribeRespProto.SubscribeResp resp(int subReqID) {
        SubscribeRespProto.SubscribeResp.Builder builder = SubscribeRespProto.SubscribeResp.newBuilder();
        builder.setSubReqID(subReqID)
                .setRespCode("0")
                .setDesc("Netty book order succeed, 3 days later, sent to the designated address");
        return builder.build();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
