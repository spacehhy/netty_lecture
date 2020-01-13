package com.hhy;

/**
 * netty开发设计者的设计策略是 遵循更为接近底层的设计策略 底层之上的东西可以由开发人员任意的自由的进行组合
 * 进行功能的组合,功能的模块化搭建 所以并没有提供非常完善的基于http的底层的基础设施
 * netty 能做的三件事
 * 1.使用netty可以作为http服务器 同tomcat一样 可以处理 请求,响应
 *   表现形式同使用 springMVC 或 struts2 web框架编写web应用是类似的
 *   只不过 netty 并没实现servlet规范(标准)
 *   像tomcat/jetty 这样的web容器(servlet)容器 必须实现servlet规范
 *   servlet规范定义了:
 *   请求什么样的,如何获取请求参数 通过request.getParameter("name")根据名字获取请求当中包含的参数的值
 *   springMVC 或 struts2底层依然是servlet 对servlet进行封装 请求参数绑定到 请求方法参数上 或 成员变量上
 *   使用netty作为http服务器 应当忘记servlet的编程方式
 *   netty 提供一种方式 相对于servlet来说是一种更底层的方式
 *   因此使用netty进行http开发 运行效率与吞吐量 一定是tomcat效率更高 (NIO)
 *   虽然tomcat本身在connector底层也会使用NIO相关的处理机制
 *   但是由于netty本身这种精巧的设计,对于高并发/大请求场景处理更游刃有余
 *   上面的介绍并不是说明netty比springMVC 或 struts2更优 把springMVC 或 struts2 贬的一文不值
 *     开发效率角度:springMVC 或 struts2 开发效率更高 框架已将底层基础构建好了,只需将相应部分完善即可
 *     比如 将controller及相应组件填写完善, 在我们创建好相应的类后,将相应代码像填空题一样将相应代码填充进去即可
 *     而netty不是这样,netty是一个非常非常底层的框架,它甚至都没有web开发中至关重要的请求路由(router) netty对请求路由没有任何支持
 *     请求路由:比如 springMVC编写web应用 controller中的某个方法上加上 @RequestMapping("/url")
 *     url可为 固定url、代参url、甚至可以是正则表达式
 *     请求url只要匹配了对应RequestMapping的url后，请求进入特定的方法； 这个url的解析是由springMVC一启动将url的映射关系处理好
 *     因此使用netty编写严肃的商业项目,请求路由需要有开发人员单独做,进行处理。
 *     GitHub上存在第三方项目对请求路由进行了封装，使得开发人员可以通过注解/配置文件的方式,对请求路由进行映射
 *
 *  2.使用netty可以作为socket开发 RPC的一种方式 remote file copy，即远程文件拷贝
 *    http是构建在TCP传输控制协议之上的应用层协议 netty可以直接接触到更为底层的socket相关的信息
 *    使得我们可以通过客户端与服务器端来通过socket方式进行调用 netty在这块的表现是netty应用的最为广泛的领域
 *    业界很多的开源项目闭源项目都在一定程度上使用netty作为底层传输的基础框架 可以在netty提供的传输机制之上
 *    用户自定协议,规范 (netty是:异步,事件驱动,协议话的客户端与服务器端开发的网络框架)
 *    socket相关开发其实也是微服务相关开发的绕不过去的一个重点 微服务:很多很多的服务部署,服务与服务之间存在进行调用
 *    调用方式可以采用http方式调用,肯定是可以的,效率上会大打折扣。
 *    所以不管像阿里得到 Dubbo(不再维护) 现在是HSF High Speed Framework,也有人戏称“好舒服”)
 *    p.s. https://www.cnblogs.com/langtianya/p/5720275.html HSF完胜dubbo
 *    像Dubbo 这样经典的分布式的一站式的框架 底层就是使用netty进行传输 spark底层使用 netty进行传输
 *    所以netty在这个领域在这个领域上表现上的非常非常优异
 *    socket开发 支持自定义协议,既规范请求有哪些部分组成 比如请求头是什么,长度是多少,请求体是什么,结束的符号等
 *
 *  3.使用netty可以支持长连接开发(与http开发相关)
 *    http协议 是一种无状态的 基于请求和响应模式的协议
 *    不管是http1.0或者http1.1本身都是一种短连接 虽然http1.1在建立好之后在一定时间之内会保持连接,过了一定时间(keepAlive)之后
 *    连接自然而然断掉,当然现在大力倡导的 Google 提出的 Http 2 在很大程度上解决这个问题 但是当http2还没有彻底流行起来情况下
 *    要想使用长连接进行一些  比如 消息推送,在线聊天 这样一些需要实时,时刻保证并且实现 服务器端向客户端推送数据的 这样一些场景下
 *    绝大多数情况下我们都会使用webSocket;
 *    webSocket:是在HTML5规范中的组成部分,它可以实现客户端(浏览器)与服务器之间的持续连接,连接一旦建立好之后,如果没有意外因素,
 *    通常情况下,理想情况下,连接会一直保持;连接一直保持的好处是:不仅客户端可以向服务器端去请求数据,得到服务器响应;服务器端反过来
 *    也可以向客户端推送数据,这个在传统的http1.0和http1.1时代是不可能实现的。在那个时代要实现这种长连接无外乎要使用轮询或者commit
 *    一些workaround的一些方式，轮询的坏处，弊端可想而知，很多时候轮询时候都是空轮询并没有实际的数据的返回，然而由于http协议的要求，
 *    每一次我们发出请求的时候都要携带相关的header信息，头信息；但是对于实际的业务来说，很多时候这个头信息是没有意义的、不需要，但不需要
 *    也得传，因为这是协议本身规定的。对于webSocket长连接来说就不是这样的,一旦连接建立好之后,不管是客户端向服务器端发送数据,还是服务器端
 *    反过来向客户端推送的数据,它们可以仅仅传递数据本身,而不必传递与头相关的信息,换句话说,它们传递的数据就是对方真正需要的数据,此外没有任何冗余的存在
 *
 *  netty设计精巧,研究源码时会被精巧设计所折服!
 *  学习的要点: 学习新技术一开始的时候切记切记不要陷入任何具体的细节当中,因为会极大的阻碍学习的速度,而且会对自信心产生极大的影响和干扰
 *              netty框架本身 从设计角度和底层实现角度来看挺复杂的,一开始就研究整个的执行流程,相信会陷入细节,反而对netty本身提供的功能
 *              关注的非常少了,不是好的做法,先把netty能做的几件事情,用代码的方式分别实现出来,让大家迅速对netty所能提供的功能以及所能完成的事情
 *              有非常直观深刻的理解,从使用netty的角度必须掌握内容
 *
 *  netty的编写流程 无论是简单的程序还是复杂的程序,总体编写代码的方式,流程都是不变的
 *  主要有三个步骤
 *  一、定义相应的 bossGroup 和 workerGroup (parent和child) 构建两个EventLoopGroup (NIOEventLoopGroup)
 *  二、ServerBootstrap netty提供的便捷的简化类 便于很轻松的启动服务器通过 childHandler 指定一个ChannelInitializer对象
 *     通过ChannelInitializer 的 initChannel方法当中关联请求流程当中所涉及到的诸多的 handler 包括(netty提供的和开发人员自定义的handler)
 *     我们按照顺序把handler添加进去,
 *  三、在我们自己提供的handler当中,我们去复写netty所提供的特定的事件回调方法
 *
 */