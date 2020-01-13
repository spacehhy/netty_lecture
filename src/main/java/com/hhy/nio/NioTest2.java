package com.hhy.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * 该例子是展现的是传统IO,如何调用它相应的方法 切换至Nio对象
 */
public class NioTest2 {

    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("NioTest2.txt");
        FileChannel fileChannel = fileInputStream.getChannel();
        //不管读还是写,buffer对象是一定要有的,buffer对象是必须与Channel对象关联起来的
        ByteBuffer byteBuffer = ByteBuffer.allocate(512);//分配一个长度为512字节的ByteButter实际512是底层数组的大小
        fileChannel.read(byteBuffer);//将文件内容读到buffer当中
        //反转/读写切换,完成三个变量的修改,limit、position、mark
        byteBuffer.flip();

        while (byteBuffer.remaining() > 0) {//剩多少个
            byte b = byteBuffer.get();
            System.out.println("Character: " + (char)b);
        }

        fileInputStream.close();
    }
}
