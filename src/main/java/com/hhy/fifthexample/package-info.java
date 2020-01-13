package com.hhy.fifthexample;

/**
 * 用netty实现的websocket长连接的 demo
 * webSocket: 它是HTML5规范的一部分 一并提出来的
 * webSocket是解决http协议本身存在的一些致命的问题,不足.
 * http协议 是一种无状态的 基于请求和响应模式的协议
 * 1.无状态 相同的客户端第一次发出请求,收到响应;紧接着再去发送请求时,这两次请求之间,实际上是没有任何关系的
 *   服务器也不会任务,第二次请求是由同一个客户端发过来的,无法识别出来,因此根本无法追踪某一个请求来自于哪一个
 *   客户端,这个客户端之前是不是在服务器存在了一些信息,web编程中经常用到的 cookie 和 session,就是为了解决这样
 *   一个问题而应运而生的,session客户端会存在一个对应的cookie值,通过响应的cookieId(cookie key)跟服务端sessionId
 *   做一个关联,当下一次请求发过来的时候,客户端(浏览器)会把相同域下的所有cookie一并发送给对应的服务器端,服务器端
 *   会收到对应的cookie信息,获取到在服务器保存的 session或者其他放在redis,一些服务器端用来保存用户状态的一些基本的
 *   技术,那么在服务器端会检索到一些与这个用户相关的一些值,就像我们常用的 登陆,网站的购物车;都是通过这样的一种方式
 *   来实现
 * 2.基于请求和响应模式的协议 http协议,请求的发起方一定是客户端,比如说浏览器,一定是浏览器首先向服务器端去发出一个
 *   请求,那服务器端收到请求,那么在发出请求之前,一定会实现客户端与服务器端先建立好一个连接,那么请求和响应都是在这个
 *   连接之上进行的,当这个连接一旦建立好之后,那么客户端就会向服务器端发送请求数据,服务器端收到请求数据之后,会进行相应
 *   的处理,比如对于servlet来说,doGet或doPost等等一系列的方法中进行相应的处理,处理会调用相应的业务方法,处理完毕之后,服
 *   务器端就会构建出相应的response响应对象,接下来将响应对象返回给客户端,而如果是基于http1.0这种协议的话,当服务器把信息
 *   返回给客户端之后,连接就失去了(连接立刻就断掉了),当客户端再次向服务器端发送请求时,就会再次建立新的连接,重复这个过程
 *   不断反复,如果是基于http1.1协议,http1.1增加了一个新特性,叫做keepAlive,表示的是客户端可以和服务器端在短时间之内保持
 *   一个连接,这个连接我们称之为 持续连接 persistent connection 什么叫持续连接呢?就是客户端与服务器端先建立好连接,客户端
 *   向服务器端发出请求,服务器端向客户端返回一个响应,比如在2秒或3秒时间内(时间可以指定),在一定的指定时间范围之内,如果客户
 *   端还会向服务器端发送请求,那么这个时候客户端是不会再与服务器端再次建立新的连接,而是重用既有的连接,服务器端也是在既有
 *   的连接之上,把对应的结果返回给客户端,如果过了一段时间之后,客户端不再向服务器端发送任何数据的话,那么持续保持了一定时间
 *   的连接会自动关闭掉,当下一次,客户端向服务端发送请求的时候,还要去建立一个新的连接.
 *   Google 提出了 http2 新的标准, 可以实现长连接
 *
 *   由于http协议的局限性,实际上存在着一些问题,这些问题使得我们的业务场景没法实现,举个最典型的例子
 *   网页聊天(通过浏览器的聊天程序是基于http的)这样的一个程序就实现不了,因为网页聊天客户端要向服务器端发送一个数据,但是
 *   服务器端如何将它接收到的某一个客户端向它发来的数据,然后广播给其他客户端呢?这种情况下需要实现服务器端推技术,服务器要
 *   将数据主动的推向各个客户端,这个对于http1.0和http1.1来说是不可能实现的,因为协议从根本上就禁止这么做,所以呢早年呢就出现
 *   了一些假的长连接技术
 *   1.轮询(客户端 每2秒 向服务器发起请求,检查服务器是否接受到新数据,如果有服务器端通过响应返回给客户端)
 *   2.commit 本质基于轮询
 *   这种方式呢 在一定程度上能满足业务场景,存在很多问题
 *   1.消息一定不是及时到达客户端的,因为它一定要下一次轮询发起的时候,服务器端所存在的消息才会返回给客户端,所以这只能说是
 *   一种准实时的消息推送
 *   2.客户端每经过一段时间就像服务器端发起一个查询,但是在很多很多情况下,这种请求实际上都是没有结果的,换句话说,如果服务
 *   器端是没有任何数据要推送给客户端,客户端实际上是不知道这件事情的,因此客户端每隔一段时间发起一次请求,而服务器端返回的
 *   结果是空,表示服务器端是没有数据要推给客户端,客户端还是要每隔一段时间做一次这样的事情,这会行成非常非常大的一种资源和
 *   网络带宽的一种浪费,因为大多数的查询都是没有结果的,另外还有一点,由于http协议本身包含了两部分header头信息 body体信息
 *   (真正的数据信息) 其实每一次轮询就想得到数据本身,但是由于http协议本身的要求和规定,每次请求要把请求头信息带过去,每次
 *   服务器端返回结果的时候,不管是真的有结果还是空的结果,服务器端都要把头信息返回给客户端,而客户端根本不关注这些信息,很多
 *   情况系,头信息占据的容量(大小)远远超过了要发送的内容本身,这其实又是一种很得不偿失的情况,因此在早年,在webSocket规范还没
 *   有出现之前,这种方式也是得到大量应用的,因为只有这种办法在一定程度上缓解这个问题,无法从根本上解决
 *
 *   自从HTML5 WebSocket规范出现之后,一切都改变了,WebSocket本身就可以实现,浏览器与服务器端之间的长连接,这种长连接是真正意
 *   义的长连接,
 *   长连接:1.客户端 与 服务端 一旦建立连接 如果没有其他因素干扰的情况下 保持这个连接不断掉 双方成为对等实体  随意 互相
 *   发送数据 实现服务器端 push技术 2.初次建立长连接 发送头信息  一旦建立成功 只传递数据 不再传递头信息 节省了网络带宽
 *
 *   升级版 http 协议   初次建立连接 是标准的http协议 只是在头信息中加入 websocket相关信息
 *   1.双向的数据传递  全双工消息传递
 *   2.基于http协议
 *   3.虽然基于http协议 也可以用在非浏览器场合 与服务器长连接  库 rocket socket  网页端 js就可以实现
 *     在 android,ios App上面可以通过一些第三方的库,原生方式与服务器端的长连接技术
 *     ios  rocketSocket 库实现 手机与服务器端长连接
 *
 *   ws://server:port/context_path
 *   ws://localhost:9999/ws      示例的 /ws 指的是 最后的context_path
 *
 *   WebSocketFrame:  Base class for web socket frames (web socket帧的一个父类)
 *   一共有六个子类,既六种frame 因为规范定义了六种frame  binary二进制数据、close发出关闭指令、
 *   continuation这一帧数据未传递完后面还会继续传、ping发心跳、pong返回心跳结果、text传递普通文本
 *   关于webSocket的 RFC https://blog.csdn.net/stoneson/article/details/8063802
 *
 *   webSocket的客户端，既可以通过html网页（javaScript方式），也可以通过一些第三方库去写
 *
 *   测试！
 *   建立连接
 *   handlerAdded: 00d861fffe113113-000005e8-00000002-0394b365d874820c-0797c4f4
 *
 *   刷新页面
 *   handlerRemoved: 00d861fffe113113-000005e8-00000001-17b113e60adf39b6-0b860b39
 *   handlerAdded: 00d861fffe113113-000005e8-00000002-0394b365d874820c-0797c4f4
 *
 *   打开浏览器控制台，发现发送两个请求
 *   1.http://localhost:63342/netty_lecture/src/webApp/webScoket.html?_ijt=2glldrg8iuiar8fugaftp3e3jn
 *     Status Code: 200 OK    Connection: keep-alive
 *   2.ws://localhost:8899/ws
 *     Status Code: 101 Switching Protocols （切换协议）  Connection: Upgrade  Upgrade: websocket
 *     （表示虽然访问的是一个ws请求，本身需要用http向服务器端建立好一个连接，连接建立好之后就Upgrade到websocket协议上，
 *     相当于连接升级，最终连接由http升级到webSocket ）
 *
 *   clint包 是我自己网上找的netty webSocket客户端
 *
 */