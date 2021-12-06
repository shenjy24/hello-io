package com.jonas.serialization;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.msgpack.annotation.Message;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * 用于测试Java序列的不足
 * User
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-23
 */
@Data
@Message
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = -1L;
    private String userName;
    private int userID;

    public byte[] codeC() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        byte[] value = userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(userID);
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }

    public byte[] codeC(ByteBuffer buffer) {
        buffer.clear();
        byte[] value = userName.getBytes();
        buffer.putInt(value.length);
        buffer.put(value);
        buffer.putInt(userID);
        buffer.flip();
        byte[] result = new byte[buffer.remaining()];
        buffer.get(result);
        return result;
    }
}
