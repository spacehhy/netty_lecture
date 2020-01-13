package com.hhy.secondexample;

/**
 * 这是一个基于rcp的请求socket编程的demo
 * 首先客户端不重写 channelActive 方法
 * 启动服务端,再启动客户端,观察发生什么事情,有没有数据的传输,
 * 如果没有数据的传输的话,如何触发客户端与服务端 开始,发送数据
 * 查看端口 netstat -aon|findstr "8899" (windows)
 * 原因分析:
 * 客户端与服务端的channelRead0 一直处于等待读取状态,需要一条数据打破僵局
 * 那么就需要确定打破僵局的时机, channelActive 表示通道已经处于活动状态
 * 通道已经连接了,连接好了之后,channelActive自动得到一次回调,因此在客户端
 * channelActive方法中想服务端发送一条数据,就会触发服务器端的 channelRead0
 * 服务端向客户端发送一条数据,触发客户端 channelRead0 客户端向服务端发送一条数据
 * 实际demo是一个停不下来的双向数据交互
 *
 * server:
 * /127.0.0.1:6761, 来自于客户端的问候!
 * /127.0.0.1:6761, from client 2019-07-11T11:40:25.187
 * /127.0.0.1:6761, from client 2019-07-11T11:40:25.188
 * /127.0.0.1:6761, from client 2019-07-11T11:40:25.189
 * /127.0.0.1:6761, from client 2019-07-11T11:40:25.189
 * /127.0.0.1:6761, from client 2019-07-11T11:40:25.190
 *
 * client:
 * localhost/127.0.0.1:8899
 * client output from server 0217e587-70a4-4f3e-9ec4-cecceb67d5f7
 * localhost/127.0.0.1:8899
 * client output from server e60c43ce-5178-4217-a107-cd1b4168be89
 * localhost/127.0.0.1:8899
 * client output from server 0f058a4c-0dcd-4283-b98f-375fb5646289
 *
 */