package com.jonas.serialization.protobuf.test;

import com.google.protobuf.InvalidProtocolBufferException;
import com.jonas.serialization.protobuf.proto.SubscribeReqProto;

/**
 * TestSubscribeReqProto
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-28
 */
public class TestSubscribeReqProto {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        SubscribeReqProto.SubscribeReq req1 = createSubscribeReq();
        System.out.println("before encode : " + req1.toString());
        SubscribeReqProto.SubscribeReq req2 = decode(encode(req1));
        System.out.println("after encode : " + req2.toString());
        System.out.println("assert equal : " + req2.equals(req1));
    }

    private static byte[] encode(SubscribeReqProto.SubscribeReq req) {
        return req.toByteArray();
    }

    private static SubscribeReqProto.SubscribeReq decode(byte[] body) throws InvalidProtocolBufferException {
        return SubscribeReqProto.SubscribeReq.parseFrom(body);
    }

    private static SubscribeReqProto.SubscribeReq createSubscribeReq() {
        SubscribeReqProto.SubscribeReq.Builder builder = SubscribeReqProto.SubscribeReq.newBuilder();
        builder.setSubReqID(1).setUserName("Jonas").setProductName("Netty Book").setAddress("China");
        return builder.build();
    }
}
