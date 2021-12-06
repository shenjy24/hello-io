package com.jonas.mmap;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * 内存映射示例
 *
 * @author shenjy
 * @date 2020/6/28
 * @description
 */
public class MMap {

    public static void main(String[] args) {
        try {
            testFileChannel();
            testMappedByteBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testFileChannel() throws IOException {
        long begin = System.currentTimeMillis();
        RandomAccessFile randomAccessFile = null;
        try {
            URL url = MMap.class.getClassLoader().getResource("mmap.md");
            randomAccessFile = new RandomAccessFile(url.getPath(), "rw");
            FileChannel channel = randomAccessFile.getChannel();
            ByteBuffer buff = ByteBuffer.allocate(16);

            StringBuffer content = new StringBuffer();
            while (-1 != channel.read(buff)) {
                //为读取做准备
                buff.flip();
                byte[] bytes = new byte[buff.remaining()];
                buff.get(bytes);
                content.append(new String(bytes, StandardCharsets.UTF_8));
                //为写入做准备
                buff.clear();
            }
            System.out.println("file channel cost: " + (System.currentTimeMillis() - begin));
        } finally {
            if (null != randomAccessFile) {
                randomAccessFile.close();
            }
        }
    }

    //内存映射
    public static void testMappedByteBuffer() throws IOException {
        long begin = System.currentTimeMillis();
        RandomAccessFile randomAccessFile = null;
        try {
            URL url = MMap.class.getClassLoader().getResource("mmap.md");
            randomAccessFile = new RandomAccessFile(url.getFile(), "rw");
            FileChannel channel = randomAccessFile.getChannel();
            int len = (int) randomAccessFile.length();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, len);

            byte[] bytes = new byte[1024];
            StringBuilder content = new StringBuilder();
            for (int offset = 0; offset < len; offset += 1024) {
                if (len - offset > 1024) {
                    buffer.get(bytes);
                    content.append(new String(bytes, StandardCharsets.UTF_8));
                } else {
                    byte[] leftBytes = new byte[len - offset];
                    buffer.get(leftBytes);
                    content.append(new String(leftBytes, StandardCharsets.UTF_8));
                }
            }
            System.out.println("mmap cost: " + (System.currentTimeMillis() - begin));
        } finally {
            if (null != randomAccessFile) {
                randomAccessFile.close();
            }
        }
    }
}
