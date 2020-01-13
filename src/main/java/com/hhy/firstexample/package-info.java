package com.hhy.firstexample;

/**
 * 这是一个使用netty构建httpServer的demo
 *
 * 测试方法  启动HttpServer
 * cmd进入命令窗口 或 postman 或 Linux系统访问
 * curl "http://localhost:8899"
 * curl -X POST "http://localhost:8899"
 * curl -X PUT "http://localhost:8899"
 * curl -X DELETE "http://localhost:8899"
 * 打开浏览器 输入 http://localhost:8899
 * 会出现请求两次的问题 即
 * 一次 localhost请求
 * 一次 favicon.ico请求 (部分浏览器如 Google 获取网站的图标)
 * favicon.ico请求的具体查找方式:
 * 1.首先查找当前网页所在服务端相同的目录 名字固定为favicon.ico文件
 * 2.如果找不到 回去网站根目录进行查找
 * 3.如果上述都找不到 图标 就会显示默认图标
 *
 * channelRegistered   : 通道注册
 * channelUnregistered : 通道解除注册
 * channelActive       : 通道处于活动状态
 * channelInactive     : 通道处于不活动状态
 * channelRead         : 通道读(获取客户端发过来的请求)
 * channelReadComplete : 通道读完成
 * userEventTriggered  : 用户事件触发器
 * channelWritabilityChanged :通道可写改变
 * exceptionCaught     : 抛出异常
 *
 * handlerAdded        : 新的处理器/通道添加
 * handlerRemoved      : 处理器/通道移除
 *
 * 浏览器请求netty 触发 handlerAdded -> channelRegistered -> channelActive
 * 当浏览器关闭 触发 channelInactive -> channelUnregistered -> handlerRemoved
 * 原因:(tomcat/netty)底层 ServerSocket不断死循环
 * http1.1协议 有keepAlive这样一个时间 比如3秒 3秒到了客户端没有发送请求,服务器端主动关闭连接
 * http1.0协议 短连接协议 请求后服务器关闭连接
 * channelRead 获取客户端发过来的请求 请求处理完之后 返回响应
 * 如果是运行在springMVC的程序 运行在tomcat或jetty这些servlet容器上 由容器保证相应的连接会被自动关闭掉
 * netty可以自己 判断客户端请求完成之后,紧接着判断请求是否是1.1,keepAlive的时间是多长时间 之后服务器主动关闭连接
 *
 */