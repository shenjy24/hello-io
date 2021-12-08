package com.jonas.example.tomcat.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

public class Response {
    //SocketChannel的封装
    private final ChannelHandlerContext ctx;
    private final HttpRequest req;

    public Response(ChannelHandlerContext ctx, HttpRequest req) {
        this.ctx = ctx;
        this.req = req;
    }

    public void write(String out) {
        try {
            if (null == out || 0 == out.length()) {
                return;
            }
            //设置HTTP及请求头信息
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
                    Unpooled.wrappedBuffer(out.getBytes(StandardCharsets.UTF_8)));
            response.headers().set("Content-Type", "text/html;");
            ctx.write(response);
        } finally {
            ctx.flush();
            ctx.close();
        }
    }
}
