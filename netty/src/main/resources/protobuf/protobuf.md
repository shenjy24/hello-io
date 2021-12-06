## Protobuf 的简介
protobuf是Google开发出来的一个语言无关、平台无关的数据序列化工具，在rpc或tcp通信等很多场景都可以使用。

通俗来讲，如果客户端和服务端使用的是不同的语言，那么在服务端定义一个数据结构，通过protobuf转化为字节流，再传送到客户端解码，就可以得到对应的数据结构。这就是protobuf神奇的地方。

并且它的通信效率极高，一条消息数据，用protobuf序列化后的大小是json的10分之一，xml格式的20分之一，是二进制序列化的10分之一。

### Protobuf 的优点
Google的Protobuf的优点如下：
- 在Google内部长期使用，产品成熟度高
- 跨语言、支持多种语言，包括C++、Java和Python
- 编码后的消息更小，更加有利于存储和传输
- 编解码的性能非常高
- 支持不同协议版本的前向兼容
- 支持定义可选和必选字段
- protobuf将数据序列化为二进制之后，占用的空间相当小，基本仅保留了数据部分，而xml和json会附带消息结构在数据中
- protobuf使用起来很方便，只需要反序列化就可以了，而不需要xml和json那样层层解析

### Protobuf 与 JSON 的比较
虽然Json用起来的确很方便，但相对于protobuf数据量更大些。无论是移动端还是PC端应用，为用户省点流量还是很有必要的，减少数据传输量不仅可以节约带宽而且可以更快地得到响应，提升用户体验。

##### Protobuf 相比 JSON 的优点
- 跟JSON相比Protobuf性能更高，更加规范
- 编解码速度快，数据体积小
- 使用统一的规范，不用再担心大小写不同导致解析失败等问题

##### Protobuf 相比 JSON 的劣势
- 改动协议字段，需要重新生成文件
- 数据没有可读性

## Protobuf 的入门
Protobuf是一个灵活、高效、结构化的数据序列化框架，相比XML等传统的序列 化工具，它更小、更快、更简单。

Protobuf支持数据结构化一次可以到处使用，甚至跨语言使用，通过代码生成工具可以自动生成不同语言版本的源代码，甚至可以在使用不同版本的数据结构进程间进行数据传递，实现数据结构的前向兼容。

### Mac 环境安装
##### 安装Protobuf
```
$ brew install protobuf
```

##### 查看版本
```
$ protoc --version
libprotoc 3.7.1
```

### 编写proto文件
##### SubscribeReq.proto
```proto
syntax = "proto2";
package netty;
option java_package = "com.jonas.serialization.protobuf.proto";
option java_outer_classname = "SubscribeReqProto";

message SubscribeReq{
    required int32 subReqID = 1;
    required string userName = 2;
    required string productName = 3;
    required string address = 4;
}
```

##### SubscribeResq.proto
```proto
syntax = "proto2";
package netty;
option java_package = "com.jonas.serialization.protobuf.proto";
option java_outer_classname = "SubscribeRespProto";

message SubscribeReq{
    required int32 subReqID = 1;
    required string respCode = 2;
    required string desc = 3;
}
```

### 编译文件
在项目根目录下执行以下命令：
```
➜ protoc --java_out=netty/src/main/java netty/src/main/resources/protobuf/SubscribeReq.proto 
➜ protoc --java_out=netty/src/main/java netty/src/main/resources/protobuf/SubscribeResp.proto
```

同时添加Maven依赖：
```xml
<dependency>
    <groupId>com.google.protobuf</groupId>
    <artifactId>protobuf-java</artifactId>
    <version>3.11.4</version>
</dependency>
```

### 测试
```java
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
```

### Netty的Protobuf开发
代码例子可以参考 [https://github.com/shenjy24/jackal-io](https://github.com/shenjy24/jackal-io) `netty/serialization/protobuf`目录下的示例。

### Protobuf 的使用注意事项
ProtobufDecoder 仅仅负责解码，不支持读半包。因此，在ProtobufDecoder前面一定要有能够处理读半包的解码器，有以下三种方式可以选择：
1. 使用Netty提供的 ProtobufVarint32FrameDecoder，它可以处理半包消息。
2. 继承Netty提供的通用半包解码器LengthFieldBasedFeameDecoder。
3. 继承ByteToMessageDecoder类，自己处理半包消息。

