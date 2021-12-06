package com.jonas.protocol.diy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NettyMessage
 * 心跳消息、握手请求和握手应答消息都统一用NettyMessage承载
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-04-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NettyMessage {
    private Header header;  //消息头
    private Object body;    //消息体
}
