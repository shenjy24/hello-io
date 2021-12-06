package com.jonas.serialization.xml.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;

import java.io.StringWriter;
import java.nio.charset.Charset;

/**
 * AbstractHttpXmlEncoder
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-04-01
 */
public abstract class AbstractHttpXmlEncoder<T> extends MessageToMessageEncoder<T> {
    IBindingFactory factory;
    StringWriter writer;
    final static String CHARSET_NAME = "UTF-8";
    final static Charset UTF_8 = Charset.forName(CHARSET_NAME);

    protected ByteBuf encode0(ChannelHandlerContext ctx, Object body) throws Exception {
        factory = BindingDirectory.getFactory(body.getClass());
        writer = new StringWriter();
        IMarshallingContext context = factory.createMarshallingContext();
        context.setIndent(2);
        context.marshalDocument(body, CHARSET_NAME, null, writer);
        String xml = writer.toString();
        writer.close();
        ByteBuf encodeBuf = Unpooled.copiedBuffer(xml, UTF_8);
        return encodeBuf;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (null != writer) {
            writer.close();
        }
    }
}
