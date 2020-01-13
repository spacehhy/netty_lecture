package com.hhy.nio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class NioTest4 {

    public static void main(String[] args) throws Exception {
        FileInputStream inputStream = new FileInputStream("input.txt");
        FileOutputStream outputStream = new FileOutputStream("output.txt");

        FileChannel inputChannel = inputStream.getChannel();
        FileChannel outputChannel = outputStream.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);//4

        while (true) {
            buffer.clear();//如果注释该行代码会发生什么情况? read第一次为0,然后read一直为0循环,output.tet文件会很大
            /**
             * 初始化
             *  []      []      []      []      []      []      {}
             *                                               capacity
             *                                                limit
             *  position
             *
             * 第一次循环:
             * 假设第一次循环就获取所有的字节数据,limit就会指向最后位置的下一个位置;
             * （1）read
             *  [1]      [2]      [3]      [4]      []      []      {}
             *                                                    capacity
             *                                                     limit
             *                                  position
             * （2）flip  调用flip方法后,position指向0(最初的状态);
             *  [1]      [2]      [3]      [4]      []      []      {}
             *                                                    capacity
             *                                    limit
             * position
             * (3)write  调用write方法把buffer所有的数据写到channel当中。
             *  [1]      [2]      [3]      [4]      []      []      {}
             *                                                    capacity
             *                                    limit
             *                                   position
             *  ===========================================================
             *  (一)如果在下一次循环前执行clear clear方法重置position和limit为初始状态
             *  [1]      [2]      [3]      [4]      []      []      {}
             *                                                    capacity
             *                                                     limit
             *  position
             *
             * [关于Channel的read方法 ReadableByteChannel]:
             * @return  The number of bytes read, possibly zero, or <tt>-1</tt> if the
             *          channel has reached end-of-stream
             * 返回值是已经读取的字节数,可能为0或者-1(如果到达了流的结尾值就为-1)
             *
             * (二)如果在下一次循环前没有执行clear
             *  [1]      [2]      [3]      [4]      []      []      {}
             *                                                    capacity
             *                                    limit
             *                                   position
             * 由于position=limit,Channel无法再进行读取,并无法将Channel的值赋值到buffer
             * 因此int read = inputChannel.read(buffer); 中的int为 0,而buffer中是缓存了
             * 最后一次读取的数据,因此:
             * （2）flip  调用flip方法后,position指向0(最初的状态);
             *  [1]      [2]      [3]      [4]      []      []      {}
             *                                                    capacity
             *                                    limit
             * position
             * (3)write  调用write方法把buffer所有的数据写到channel当中。
             *  [1]      [2]      [3]      [4]      []      []      {}
             *                                                    capacity
             *                                    limit
             *                                   position
             *  因为read=0而无法等于-1,因此死循环,一直将buffer中最后一次缓存的数据写入到输出通道(文件)当中
             *  总结:
             *  通过NIO读取文件涉及到3个步骤:
             *  1.从FileInputStream获取到FileChannel对象。
             *  2.创建Buffer。
             *  3.将数据从Channel读取到Buffer中。
             *
             *  绝对方法与相对方法的含义:
             *  1.相对方法:limit值和position值会在操作时被考虑到。put()/get()
             *  2.绝对方法:完全忽略掉limit值与position值。put(index)/get(index)
             */

            int read = inputChannel.read(buffer);

            System.out.println("read: " + read);

            if (read == -1) {
                break;
            }

            buffer.flip();

            outputChannel.write(buffer);
        }
        inputChannel.close();
        outputChannel.close();
        inputStream.close();
        outputStream.close();
    }
}
