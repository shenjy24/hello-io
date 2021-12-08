package com.jonas.example.tomcat;

import com.jonas.example.tomcat.http.Request;
import com.jonas.example.tomcat.http.Response;
import com.jonas.example.tomcat.servlet.Servlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Tomcat {

    private final int port = 8080;
    private final Map<String, Servlet> servletMapping = new HashMap<>();
    private final Properties webXml = new Properties();

    public static void main(String[] args) {
        new Tomcat().start();
    }

    public void start() {
        init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new TomcatHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = server.bind(port).sync();
            System.out.println("Tomcat 已启动，监听的端口是：" + port);
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private void init() {
        //加载web.properties文件，同时初始化servletMapping
        try {
            String webPath = this.getClass().getResource("/tomcat").getPath();
            FileInputStream fis = new FileInputStream(webPath + "/web.properties");
            webXml.load(fis);

            for (Object k : webXml.keySet()) {
                String key = k.toString();
                if (key.endsWith(".url")) {
                    String servletName = key.replaceAll("\\.url$", "");
                    String url = webXml.getProperty(key);
                    String className = webXml.getProperty(servletName + ".className");
                    Servlet obj = (Servlet) Class.forName(className).newInstance();
                    servletMapping.put(url, obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class TomcatHandler extends ChannelHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;
                Request request = new Request(ctx, req);
                Response response = new Response(ctx, req);
                //实际业务处理
                String url = request.getUrl();
                if (servletMapping.containsKey(url)) {
                    servletMapping.get(url).service(request, response);
                } else {
                    response.write("404 - Not Found");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }
    }
}
