package com.hhy.nio;

import java.nio.ByteBuffer;

/**
 * ByteBuffer类型化的put与get方法
 */
public class NioTest5 {

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(64);

        buffer.put((byte) 64);
        buffer.putShort((short) 2);
        buffer.putInt(15);
        buffer.putLong(500000000L);
        buffer.putDouble(14.123456);
        buffer.putFloat((float) 3.14);
        buffer.putChar('字');

        buffer.flip();

        System.out.println(buffer.get());
        System.out.println(buffer.getShort());
        System.out.println(buffer.getInt());
        System.out.println(buffer.getLong());
        System.out.println(buffer.getDouble());
        System.out.println(buffer.getFloat());
        System.out.println(buffer.getChar());

        /**
         * 如果读取的类型不对,或者读取的数量大于buffer中数量
         * 将抛出java.nio.BufferUnderflowException
         * System.out.println(buffer.getInt());
         */
    }
}
