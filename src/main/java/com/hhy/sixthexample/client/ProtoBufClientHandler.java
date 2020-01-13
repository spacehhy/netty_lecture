package com.hhy.sixthexample.client;

import com.hhy.sixthexample.MyMessageInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Random;

public class ProtoBufClientHandler extends SimpleChannelInboundHandler<MyMessageInfo.MyMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MyMessageInfo.MyMessage msg) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        int randomInt = new Random().nextInt(3);

        MyMessageInfo.MyMessage myMessage = null;
        if (0 == randomInt) {
            myMessage = MyMessageInfo.MyMessage.newBuilder()
                    .setDataType(MyMessageInfo.MyMessage.DataType.PersonType)
                    .setPerson(MyMessageInfo.Person.newBuilder().setName("张三").setAge(23).setAddress("北京").build())
                    .build();
        } else if (1 == randomInt) {
            myMessage = MyMessageInfo.MyMessage.newBuilder()
                    .setDataType(MyMessageInfo.MyMessage.DataType.DogType)
                    .setDog(MyMessageInfo.Dog.newBuilder().setName("汪汪").setAge(1).build())
                    .build();
        } else {
            myMessage = MyMessageInfo.MyMessage.newBuilder()
                    .setDataType(MyMessageInfo.MyMessage.DataType.CatType)
                    .setCat(MyMessageInfo.Cat.newBuilder().setName("喵喵").setCity("深圳").build())
                    .build();
        }
        ctx.channel().writeAndFlush(myMessage);
    }
}
