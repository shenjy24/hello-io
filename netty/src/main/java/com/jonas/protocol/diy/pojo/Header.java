package com.jonas.protocol.diy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息头
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-04-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Header {
    private int crcCode = 0xabef0101;
    //消息长度
    private int length;
    //会话ID
    private long sessionID;
    //消息类型
    private byte type;
    //消息优先级
    private byte priority;
    //消息附件
    private Map<String, Object> attachment = new HashMap<>();
}
