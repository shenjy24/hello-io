package com.jonas.feature;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class ByteBufDemo {

    public void testByteBuf() {
        // 使用默认的分配器分配了一个初始容量为9、最大限制为100个字节的缓冲区
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10, 100);
        System.out.println(buffer);
        // 写入数据
        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        System.out.println("写入4个字节结束");
        // 测试获取，不改变指针位置
        for (int i = 0; i < buffer.readableBytes(); i++) {
            System.out.println(buffer.getByte(i));
        }
        System.out.println("====================");
        // 测试读取数据
        while (buffer.isReadable()) {
            System.out.println(buffer.readByte());
        }
    }
}
