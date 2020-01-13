package com.hhy.thrift;

/**
 * Apache Thrift(节约的,节俭的,使数据体积更小) 可伸缩跨语言(支持语言比ProtoBuf多)开发
 * 最早是由Facebook开发出来的,后来捐献给Apache,很快成为Apache顶级项目
 * Apache官网:http://apache.org/      Thrift官网:http://thrift.apache.org/
 * Mac 了解homebrew https://brew.sh/
 * 官方文档:http://thrift.apache.org/docs/idl
 * 我们使用Google ProtoBuf主要是用来定义一些消息(Message),真正的传输载体用Netty传输,
 * Thrift除了可以定义Strut结构体(消息),还可以在Thrift中定义消息,Thrift本身也提供了传输层次,(提供了服务端和客户端机制)也可以将Netty作为消息底层传输的载体
 * 来完成服务端与客户端的双向数据传递。
 *
 * Thrift IDL 文件的编写
 * Thrift 支持的组件有三类，strut结构体->编译完生成类,service->编译完生成表示服务端与客户端间通信用到的接口,
 *                      exception->编译完生成表示服务端与客户端间通信用到的接口所抛出来的异常。(方法名,参数,可选返回类型,可能抛出的异常)
 *  Thrift本身有自己服务器和自己的客户端,我们在Thrift整个的框架之下去编写对应的Strut,编写service,编写exception;然后通过Thrift本身提供的客户端框架和
 *  服务端框架,加进去,启动客户端,启动服务端,就可以通信了
 *  分布式开发,企业内部通讯首选的一个框架,实现两个系统之间的RPC调用
 *
 *  生成代码:两大块工作
 *  1.编写.thrift文件
 *  2.第一步生成对应的(java)代码;第二步如果程序中想要调用,需要引入thrift相关jar包
 *
 *  thrift --gen <language> <Thrift filename>
 *  thrift --gen java src/thrift/data.thrift
 *  将生成代码放到项目相应位置,引入jar包 Gradle: 'org.apache.thrift:libthrift:0.12.0',
 *
 *  生成代码后,会生成一个gen-java目录,我们如何规避掉这个目录?
 *
 *  如忘记了具体内容,参考盛思源netty视频19期
 *
 *  解决Thrift对于Log4j的依赖,
 *  1.添加依赖  'org.apache.logging.log4j:log4j-slf4j-impl:2.11.2',
 *  2.添加配置文件    log4j2.xml
 *
 * /





