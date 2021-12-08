package com.jonas.example.rpc.provider;

import com.jonas.example.rpc.api.RpcHelloService;

public class RpcHelloServiceImpl implements RpcHelloService {
    @Override
    public String hello(String name) {
        return "Hello " + name + "!";
    }
}
