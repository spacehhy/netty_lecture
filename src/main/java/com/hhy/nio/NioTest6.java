package com.hhy.nio;

import java.nio.ByteBuffer;

/**
 * 分割/分片buffer(slice:左闭右开)
 * Slice Buffer与原有buffer共享相同的底层数组
 */
public class NioTest6 {

    public static void main(String[] args) {
        /**
         * [][][][][][][]
         *    |      |
         *    -------
         *       |
         *    截取形成新得到buffer，
         * 操作原buffer或者截取获得的buffer中的相同元素进行修改,两个buffer都会发生改变
         */

        ByteBuffer buffer = ByteBuffer.allocate(10);

        for (int i = 0; i < buffer.capacity(); i++) {
            buffer.put((byte) i);
        }

        buffer.position(2);
        buffer.limit(6);

        ByteBuffer sliceBuffer = buffer.slice();

        for (int i = 0; i < sliceBuffer.capacity(); i++) {
            byte b = sliceBuffer.get(i);
            b *= 2;
            sliceBuffer.put(i, b);
        }

        buffer.position(0);
        buffer.limit(buffer.capacity());

        while (buffer.hasRemaining()) {
            System.out.println(buffer.get());
        }
    }
}
