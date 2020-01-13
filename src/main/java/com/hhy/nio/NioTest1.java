package com.hhy.nio;

import java.nio.IntBuffer;
import java.security.SecureRandom;

/**
 * 该例子是展现的Nio中buffer是如何实现读,写以及读写切换操作
 */
public class NioTest1 {

    public static void main(String[] args) {
        //同一个buffer既可以执行读又可以执行写
        IntBuffer buffer = IntBuffer.allocate(10);
        //写
        for (int i = 0; i < buffer.capacity(); i++) {
            int randomNumber = new SecureRandom().nextInt(20);
            buffer.put(randomNumber);
        }
        //状态翻转 实现读写切换
        buffer.flip();
        //读
        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }
    }
}
