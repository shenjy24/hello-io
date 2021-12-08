package com.jonas.example.rpc.consumer;

import com.jonas.example.rpc.api.RpcHelloService;
import com.jonas.example.rpc.api.RpcService;
import com.jonas.example.rpc.consumer.proxy.RpcProxy;

public class RpcConsumer {

    public static void main(String[] args) {
        RpcHelloService rpcHelloService = RpcProxy.create(RpcHelloService.class);

        System.out.println(rpcHelloService.hello("Tom"));

        RpcService rpcService = RpcProxy.create(RpcService.class);

        System.out.println("8 + 2 = " + rpcService.add(8, 2));
        System.out.println("8 - 2 = " + rpcService.sub(8, 2));
        System.out.println("8 * 2 = " + rpcService.mult(8, 2));
        System.out.println("8 / 2 = " + rpcService.div(8, 2));
    }
}
