package com.jonas.serialization.serializable;

import com.jonas.serialization.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

/**
 * 测试JDK序列化码流和性能
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-23
 */
public class SerializableTest {
    public static void main(String[] args) throws IOException {
        testSize();
        testPerformance();
    }

    public static void testSize() throws IOException {
        User user = new User("Jonas", 1);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bos);
        os.writeObject(user);
        os.flush();
        os.close();

        byte[] result = bos.toByteArray();
        System.out.println("The jdk serializable length is : " + result.length);
        bos.close();
        System.out.println("The byte array serializable length is : " + user.codeC().length);
    }

    public static void testPerformance() throws IOException {
        User user = new User("Jonas", 1);
        int loop = 1000000;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(user);
            os.flush();
            os.close();
            byte[] b = bos.toByteArray();
            bos.close();
        }
        long endTime = System.currentTimeMillis();
        System.out.println("The jdk serializable cost time is : " + (endTime - startTime) + " ms");

        ByteBuffer buffer = ByteBuffer.allocate(1024);
        startTime = System.currentTimeMillis();
        for (int i = 0; i < loop; i++) {
            byte[] b = user.codeC(buffer);
        }
        endTime = System.currentTimeMillis();
        System.out.println("The byte array serializable cost time is : " + (endTime - startTime) + " ms");
    }
}
