package com.hhy.thrift;

/**
 * Thrift性能是特别强的
 *
 * server
 * SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
 * SLF4J: Defaulting to no-operation (NOP) logger implementation
 * SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
 * Thrift Server Started!
 * Got Client Param: 张三
 * Got Client Param:
 * 李四
 * 24
 * true
 *
 * client
 * SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
 * SLF4J: Defaulting to no-operation (NOP) logger implementation
 * SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
 * 张三
 * 20
 * false
 * ---------
 *
 * thrift的代码看起来服务端和客户端好像是在一台机器上使用的,在一个jvm上调用,实际上底层是通过socket以RPC形式调用
 *
 * server文件中的 TServer源码解析
 * TServer 有六个实现类
 *
 * AbstractNonblockingServer   不能直接用的
 * TSimpleServer               简单的单线程服务模型,常用于测试
 * TThreadPoolServer           多线程服务模型,使用标准的阻塞式IO
 * TNonblockingServer          多线程服务模型,使用非阻塞式IO(需使用TFramedTransport数据传输方式)
 * THsHaServer                 THsHa引入了线程池去处理,其模型把读写任务放到线程池去处理;half synchronize half asynchronous 半同步半异步
 *                             的处理模式,Half-aysnc是在处理IO事件上(accept/read/write io),Half-sync用于handler对rpc的同步处理
 * TThreadedSelectorServer
 *
 * 服务器端和客户端的 TTransport(传输)/TProtocol(协议)都可以有多种选择,但是客户端与服务器端的多种选择一定要对应上,
 * 这样由一端发往另一端的数据对端才能识别出来
 *
 * PersonServiceImpl对象本身是由处理器(processor)的泛型对象指定的,new 出一个 PersonServiceImpl表示真正的数据由客户端传递过来之后,服务器端是由
 * PersonServiceImpl里面特定的方法进行调用。
 *
 * Thrift 与 Spring整合
 * PersonService.Processor<PersonServiceImpl> processor = new PersonService.Processor<>(new PersonServiceImpl());
 * 上述代码可以使用Spring的配置文件以依赖注入的形式注入进来
 *
 * Thrift 对异构平台的支持,
 * Python作为client Java作为server
 *
 * 建立python客户端.note
 * 链接：http://note.youdao.com/noteshare?id=1df665079133ac5ea0880656dc27df7f&sub=25B850B7E2FB4D8D96EFC1AEC057866B
 *
 * thrift-0.12.0.tar.gz
 *
 * RPC 很大程度上解决的是两个问题,
 * 1.解决了高效的远程调用,不像http传递文本效率低很多
 * 2.可以完成异构平台之间的调用,毕竟不是所有的代码都是java写的,实现多种语言代码的互通,除了传统的http方式,RPC就是一种非常重要的选择
 *
 * 无论是像protoBuf还是thrift 都是在大数据框架或者实际工程项目开发当中,都会得到非常广泛的应用,而且一旦应用起来都是大应用
 * 在学习了这两个RPC框架之后,新的项目技术选型的时候,不要脑子里永远存在着只有http这样一种通讯方式,http这种通讯方式比如可以实现客户端跟服务端之间的调用
 * 但如果都是部署在自己的内网当中,服务器间的通讯,基于分布式系统组件与组件之间的通讯,大家首先要考虑的是RPC的这种通讯方式,因为现在我们知道,微服务这种趋势呢
 * 越来越明显,那么RPC在微服务理念当中,扮演这非常非常重要的角色,它承载着底层,扮演着底层基础设施的一个角色,它的好与不好,性能的高与低,直接影响着我们整个上层
 * 应用的运行的效率,所以无论是protoBuf还是thrift都应该仔细的消化,理解,并去学习.
 * 比较 Google ProtoBuf 与 Apache Thrift
 * ProtoBuf 单纯的序列化与反序列化,编码和解码的库而已;它也没有提供相应的传输的载体,之前的代码中我们看到,我们通过netty提供的protoBuf的编解码方式,将netty
 *          作为transport层的组件,然后将所生成出来的Message对象,进行传递,所生产的Message作为protoc而存在
 * Thrift   比ProtoBuf更进一步,它除了可以通过IDL文件来去生成相应的对象之外,他也提供了服务器端和客户端的代码的基础设施,使得我们完全就可以使用thrift这种
 *          方式就可以实现客户端与服务器端双向的数据传递
 *
 * Google gRPC 基于protoBuf3 这个版本,不仅能够基于.proto文件生成相应的程序代码,他也会对应的生成客户端和服务器端传输层的代码,gRPC在RPC框架里的角色,地位
 *             就非常类似thrift了,基于已经非常成熟的protoBuf消息格式,并且又提供了进一步生成客户端和服务器端传输代码的,全新的RPC框架。
 *
 * 继续学习,请转到gRPC
 *
 */