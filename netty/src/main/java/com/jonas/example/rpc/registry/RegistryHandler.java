package com.jonas.example.rpc.registry;

import com.jonas.example.rpc.protocol.InvokerProtocol;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现服务注册和服务调用的功能。
 * 因为所有模块创建在同一个项目中，所以为了简化，服务端没有采用远程调用，而是直接扫描本地Class，然后利用反射调用。
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {
    //保存所有可用的服务
    public static ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<>();
    //保存所有相关的服务类
    private final List<String> classNames = new ArrayList<>();

    private final static String ROOT_PACKAGE = "com.jonas.example.rpc.provider";

    public RegistryHandler() {
        //完成递归扫描
        scanClass(ROOT_PACKAGE);
        doRegister();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        InvokerProtocol request = (InvokerProtocol) msg;

        //当客户端建立连接时，需要从自定义协议中获取信息，以及具体的服务和实参
        //使用反射调用
        if (registryMap.containsKey(request.getClassName())) {
            Object clazz = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParams());
            result = method.invoke(clazz, request.getValues());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void scanClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanClass(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    private void doRegister() {
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> i = clazz.getInterfaces()[0];
                registryMap.put(i.getName(), clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
