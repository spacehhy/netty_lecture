package com.hhy.grpc;

/**
 * Google gRPC 基于protoBuf3 这个版本,不仅能够基于.proto文件生成相应的程序代码,他也会对应的生成客户端和服务器端传输层的代码,gRPC在RPC框架里的角色,地位
 *             就非常类似thrift了,基于已经非常成熟的protoBuf消息格式,并且又提供了进一步生成客户端和服务器端传输代码的,全新的RPC框架。
 *
 * 1.相比于ProtoBuf和Thrift,gRPC的上手难度大一些,配置起来比较繁琐,很容易出错,
 * 2.gRPC优势在于:IDL文件就是protoBuf文件(protoBuf3)
 *
 * 官网:  https://grpc.io/
 * 可以实现效果:客户端与服务器端可以实现双向的流式通信,有点类似webSocket含义,建立长连接,客户端可以随意向服务器端发送数据,反之服务器端也可以随意向客户端发送
 * 数据,返回数据,它们之间彼此独立,互不相干的。
 *
 * 重要： https://grpc.io/docs/guides/concepts/ （一定要花时间去读文档）
 * Quick Start
 * https://grpc.io/docs/quickstart/java/
 *
 * （Netty：Http、TCP、Socket、WebSocket）
 * task one
 * 将Quick Start运行起来
 * GitHub地址:
 * https://github.com/grpc/grpc-java
 * 第一步
 * https://grpc.io/docs/quickstart/java/
 * $ # Clone the repository at the latest release to get the example code:
 * $ git clone -b v1.23.0 https://github.com/grpc/grpc-java
 * $ # Navigate to the Java examples:
 * $ cd grpc-java/examples
 *
 * Run a gRPC application
 * From the examples directory:
 * 1.Compile the client and server
 * $ ./gradlew installDist
 * 1.Run the server
 * $ ./build/install/examples/bin/hello-world-server
 * 1.In another terminal, run the client
 * $ ./build/install/examples/bin/hello-world-client
 * 详见 https://grpc.io/docs/quickstart/java/
 *
 * Linux 对应Vi来说 Ctrl+F 是往下翻屏,Ctrl+B 是往上翻屏
 * 第一步
 * https://grpc.io/docs/tutorials/basic/java/
 * 第二步
 * https://github.com/grpc/grpc-java
 * 第三步
 * Or for Gradle with non-Android, add to your dependencies:
 * implementation 'io.grpc:grpc-netty-shaded:1.23.0'
 * implementation 'io.grpc:grpc-protobuf:1.23.0'
 * implementation 'io.grpc:grpc-stub:1.23.0'
 * 将上述依赖copy到build.gradle配置文件上!
 *
 * Generated Code
 * For protoBuf-based codegen, you can put your proto files in the src/main/proto and src/test/proto
 * directories along with an appropriate plugin.
 *
 * For protoBuf-based codegen integrated with the Gradle build system, you can use protoBuf-gradle-plugin:
 * (https://github.com/google/protobuf-gradle-plugin)
 *
 * apply plugin: 'com.google.protobuf'
 *
 * buildscript {
 *   repositories {
 *     mavenCentral()
 *   }
 *   dependencies {
 *     classpath 'com.google.protobuf:protobuf-gradle-plugin:0.8.8'
 *   }
 * }
 *
 * protobuf {
 *   protoc {
 *     artifact = "com.google.protobuf:protoc:3.9.0"
 *   }
 *   plugins {
 *     grpc {
 *       artifact = 'io.grpc:protoc-gen-grpc-java:1.23.0'
 *     }
 *   }
 *   generateProtoTasks {
 *     all()*.plugins {
 *       grpc {}
 *     }
 *   }
 * }
 *
 * Transport
 * The Transport layer does the heavy lifting of putting and taking bytes off the wire.
 * The interfaces to it are abstract just enough to allow plugging in of different implementations.
 * Note the transport layer API is considered internal to gRPC and has weaker API guarantees than the core API under package io.grpc.
 *
 * gRPC comes with three Transport implementations:
 *
 * The Netty-based transport is the main transport implementation based on Netty. It is for both the client and the server.
 * The OkHttp-based transport is a lightweight transport based on OkHttp. It is mainly for use on Android and is for client only.
 * The in-process transport is for when a server is in the same process as the client. It is useful for testing, while also being safe for production use.
 * 一切尽在官方Git项目文档介绍里!
 *
 * 使用:
 * gradle clean
 * gradle generateProto
 * 注意: 放置proto文件的文件夹要与java目录同级
 * -java
 * -proto
 *
 * tcp连接是一种可靠的连接,实际工程中绝大多数并不是,客户端向服务端发送请求,服务器端把响应返回之后,客户端就把连接关闭掉。
 * 而是连接会一直保存起来,连接会一直建立,体现出socket编程相比于http编程的优势所在。如何知晓连接是否中断,通过一定的心跳检测机制,帮助我们去检查连接是否存活,
 * 如果通过心跳的方式发现连接不存在了,这个时候再把连接断掉,客户端发起全新的连接。
 *
 * Runtime 类 每一个jvm都存在一个单例的 Runtime类
 * Runtime.getRuntime().addShutdownHook(new Thread(()->{
 *    doSomething
 * }));
 * addShutdownHook:jvm回调钩子
 *
 *
 * grpc 四种消息类型
 *
 * 1.客户端发送消息  服务端收到 返回响应  (最简单,像方法调用)
 * [客户端向服务端发送一个对象,服务端向客户端返回一个对象]
 * 2.客户端发送数据 服务端返回 stream 流的形式  客户端检测什么时候流全部返回
 * [客户端向服务端发送一个对象,服务端向客户端返回一个流式数据(java中迭代器对象 Iterator)]
 * 3.客户端不断向服务端发送stream 流  所有流发完之后 服务器返回结果
 * [客户端向服务端发送一个流式数据,服务端向客户端返回一个单个对象]
 * 4.客户端与服务端双向流传递  双向流在两个不同的stream里面 两个stream彼此独立
 *   在每个通道内,流的顺序是确保的
 * [客户端向服务端发送一个对象,服务端向客户端返回一个流式数据(java中迭代器对象)]
 *
 * grpc 四种消息类型
 *
 * 1.客户端发送消息  服务端收到 返回响应  (最简单,像方法调用)
 * 2..客户端发送数据 服务端返回 stream 流的形式  客户端检测什么时候流全部返回
 * 3.客户端不断向服务端发送stream 流  所有流发完之后 服务器返回结果
 * 4.客户端与服务端双向流传递  双向流在两个不同的stream里面 两个stream彼此独立
 *   在每个通道内,流的顺序是确保的
 *
 * p.s. 客户端是流式的,而服务端返回一个确定的结果。blockingStub是用不了的，只能使用异步的stub。
 * 流式的双向数据传递,逻辑关系是:在两个不同的流上面进行的双向数据传递,两个流之间是完全独立的,但是一般从逻辑上面说,一方把流关闭了,另一方一般也要把流关闭掉,
 * 因为单向流意义不大,故客户端和服务端都应使用onCompleted关闭流
 *
 * 使用grpc的gradle插件 执行命令 gradle generateProto时 如何将生成的代码生成在源代码目录下
 * 1.不生成在源代码目录下,造成大量手动复制粘贴操作
 * 2.如果不进行剪切操作,项目内会存在多个重复的相同包名,相同类名的类 [错误:类重复:]
 *
 * 解决问题方法:
 * 在grpc官方提供的构建脚本 build.gradle 中的插件中寻找 相关配置 指定生成的java代码的输出目录
 * 解决方案1.百度/谷歌搜索 相关问题
 * 解决方案2.查看官方文档或demo,通过一些代码,查看理解输出目录是如何配置的
 * 突破口: com.google.protobuf:protobuf-gradle-plugin:0.8.8
 * cd .gradle
 * cd caches/modules-2/files-2.1/com.google.protobuf/protobuf-gradle-plugin/0.8.8
 * 找到 protobuf-gradle-plugin-0.8.8-sources.jar 源文件中的
 * GenerateProtoTask.groovy
 * ---- private String outputBaseDir ----
 *
 * Set the output directory for this plugin, relative to {link GenerateProtoTask#outputBaseDir}.
 * void setOutputSubDir(String outputSubDir) {
 *      this.outputSubDir = outputSubDir
 * }
 *
 * generateProtoTasks {
 *     all()*.plugins {
 *         grpc {
 *             //修改生成java文件路径
 *             outputSubDir = "helloworld"
 *         }
 *     }
 * }
 * build/generated/source/proto/main/grpc  grpc文件夹由 grpc 改为 helloworld
 *
 * 修改 outputBaseDir
 * (1) 访问 https://github.com/google/protobuf-gradle-plugin
 * (2) Change where files are generated
 * By default generated Java files are under $generatedFilesBaseDir/$sourceSet/$builtinPluginName,
 * where $generatedFilesBaseDir is $buildDir/generated/source/proto by default, and is configurable. E.g.,
 *
 * protobuf {
 *   ...
 *   generatedFilesBaseDir = "$projectDir/src/generated"
 * }
 * (3) https://stackoverflow.com/questions/32820728/simple-protobuf-compilation-with-gradle
 * 理解  generatedFilesBaseDir 为何意义
 * 顺便一提,搜索问题请使用 google 或者 stackoverflow 尽量放弃百度搜索
 *
 * generateProtoTasks.generatedFilesBaseDir = "src"
 *
 * 找到 protobuf-gradle-plugin-0.8.8-sources.jar 源文件中的
 * ProtobufConfigurator.groovy
 *
 * The base directory of generated files. The default is
 * "${project.buildDir}/generated/source/proto".
 * public ProtobufConfigurator(Project project, FileResolver fileResolver) {
 *   this.project = project
 *   if (Utils.isAndroidProject(project)) {
 *     tasks = new AndroidGenerateProtoTaskCollection()
 *   } else {
 *     tasks = new JavaGenerateProtoTaskCollection()
 *   }
 *   tools = new ToolsLocator(project)
 *   taskConfigClosures = []
 *   generatedFilesBaseDir = "${project.buildDir}/generated/source/proto"
 * }
 *
 * Closure 闭包?  待补充学习
 * ProtobufSourceDirectorySet 0.8版本内包含, 0.88版本未发现
 *
 * public ProtobufSourceDirectorySet(String name,FileResolver fileResolver){
 *     super(name, String.format("%s Proto source",name), fileResolver, new DefaultDirectoryFileTreeFactory())
 *     srcDir("src/${name}/proto")
 *     include("** /*.proto")
 * }
 *
 * grpc Nodejs环境搭建
 * 官方例子: https://grpc.io/docs/quickstart/node/
 *
 * $ # Clone the repository to get the example code
 * $ git clone -b v1.23.0 https://github.com/grpc/grpc
 * $ # Navigate to the dynamic codegen "hello, world" Node example:
 * $ cd grpc/examples/node/dynamic_codegen
 * $ # Install the example's dependencies
 * $ npm install
 * 等,详情见官网文档
 *
 * Nodejs官网: https://nodejs.org/en/
 * nvm: node version manager
 * 安装完成后查看命令 which node / which npm; node-v / npm -v
 *
 * 参考官网 https://github.com/grpc/grpc 下
 * grpc/examples/node/ 下
 * package.json
 * {
 *   "name": "grpc-examples",
 *   "version": "0.1.0",
 *   "dependencies": {
 *     "@grpc/proto-loader": "^0.1.0",
 *     "async": "^1.5.2",
 *     "google-protobuf": "^3.0.0",
 *     "grpc": "^1.11.0",
 *     "lodash": "^4.6.1",
 *     "minimist": "^1.2.0"
 *   }
 * }
 *
 * 将官网的package.json内容复制到本地package.json(相当于pom文件,build.gradle文件)中 并在terminal中 使用 npm install命令下载依赖
 * 完成后 执行 npm install 显示 audited 178 packages in 7.754s found 0 vulnerabilities
 *
 * 编写代码:
 * 相比与java和其他语言 Nodejs在编写grpc实现方面是有明显不同的。集中体现在连接和代码编写方式上，
 * grpc在node领域的实现有两种情况：
 * 1.动态代码生成：编写代码时，不需要提前由proto文件来去生成对应的js代码。java开发时需要由编译器先去生成好对应的java代码。对于动态代码生成
 *   的nodejs是不需要这么做的，那么怎么把proto文件和对应的rpc关联起来呢？它是提前指定好proto文件的位置，然后在运行的过程当中，它会动态的帮你
 *   生成这些文件(包括客户端的动态生成和服务端的动态生成)。
 * 2.静态代码生成：与java的运行原理比较类似，需要通过grpc针对nodejs的编译器，提前由对应的proto文件生成相应的js文件，生成文件后位于项目目录中
 *   接下来编写js代码时，可以引用已经生成好的stub文件，这种方式与java的方式是殊途同归的。(同样支持客户端和服务端这两种方式)
 *
 * 这两种方式没有明显的优劣区分，一个小建议,要使用动态生成就所有的代码都使用动态生成,要使用静态生成就所有的代码都使用静态生成。
 * 一、动态代码生成
 * 客户端的处理逻辑：
 * 1.首先要指定好proto文件的位置
 * 2.引入grpc使用到的第三方的库
 * 3.读取到客户端文件,通过grpc相应的方法,找到proto文件中定义的XXX服务,调用特定的方法
 *
 * 编写客户端和服务端后发现 缺少 grpc_node.node 文件 报错如下:
 * Error: Failed to load E:\font-end\grpc_demo\node_modules\grpc\src\node\extension_binary\node-v64-win32-x64-unknown\grpc_node.node.
 *   Cannot find module 'E:\font-end\grpc_demo\node_modules\grpc\src\node\extension_binary\node-v64-win32-x64-unknown\grpc_node.node'
 *
 * 解决办法 在terminal 中执行 npm rebuild
 * https://stackoverflow.com/questions/49758008/nodejs-error-failed-to-load-grpc-binary-module-because-it-was-not-installed-fo
 * It seems like you have some verion conflict (版本冲突)
 * Expected directory: node-v57-linux-x64-glibc
 * Found: [node-v59-linux-x64-glibc]
 * Have you tried running npm rebuild in your app folder?
 *
 * 解决后执行 node app/grpcClient.js命令 或 右键 Run grpcClient.js
 *
 * 二、静态代码生成
 * https://grpc.io/docs/tutorials/basic/node/
 * Example code and setup
 * grpc/grpc/examples/node/static_codegen/route_guide
 * https://github.com/grpc/grpc/tree/v1.23.0/examples/node/static_codegen
 *
 * cd ../../protos
 * npm install -g grpc-tools
 * grpc_tools_node_protoc --js_out=import_style=commonjs,binary:../node/static_codegen/
 * --grpc_out=../node/static_codegen
 * --plugin=protoc-gen-grpc=`which grpc_tools_node_protoc_plugin` helloworld.proto
 * grpc_tools_node_protoc --js_out=import_style=commonjs,binary:../node/static_codegen/route_guide/
 * --grpc_out=../node/static_codegen/route_guide/
 * --plugin=protoc-gen-grpc=`which grpc_tools_node_protoc_plugin` route_guide.proto
 * 根据自己项目修改后
 * grpc_tools_node_protoc
 * --js_out=import_style=commonjs,binary:static_codegen/
 * --grpc_out=static_codegen
 * --plugin=protoc-gen-grpc=C:\Users\Administrator\node_modules\grpc-tools\bin\grpc_node_plugin.exe proto/Student.proto
 * 注意windows与mac/linux的不同, 插件执行文件位于我的文档 node_modules文件夹内
 * everything 搜索 grpc_node_plugin.exe 找到插件可执行文件位置
 * p.s.详情见nodejs项目
 *
 * rpc领域讲解了
 * 1.pb的基本定义方式,以及在netty当中对pb的支持,通过相应的编解码器来去实现了在netty当中对与pb消息格式的一种很完善的支持,
 * 包括这种多协议的消息格式
 * 2.Apache thrift的定义方式,如何定义thrift的IDL文件,如何通过相应的thrift的编译器来去生成对应的代码,然后在服务端,客户端如何实现这种调用
 * 并且实现了python和java之间的异构平台的相互调用
 * 3.grpc的使用方式,grpc本质上还是依赖pb的,他的IDL文件就是protobuf,只不过是version3,通过相应的插件支持,在java中通过gradle,在nodejs是
 * 通过相应的npm下载安装好全局的工具来去完成,并且在nodejs中提供了,动态、静态两种生成方式,可以实现nodejs和java之间的相互调用
 * 关于rpc普遍框架应该注意的是,在使用之前准备工作的一些相似性,对于异构平台的大力支持,如何实现多语言平台之间的调用,可以通过传统的httpService
 * 或者webservice,用http这种调用方式显然是一种解决方案,然而效率更高的一种方式是这种rpc远程调用方式.
 * rpc框架客户端服务端底层是什么样子的,底层是通过一种什么方式进行通讯的,IDL文件时如何写的,对于不同的库(框架)都是不一样的,需要知道其原理,
 * 底层无外乎就是获取到数据,进行编解码,以字节方式发送给对端,对端再进行反序列化,反序列化之后再去进行解码,解码之后把相应数据拿出来,至于编解码
 * 它们要共同遵守之前定义好的格式/协议(对于protobuf就是一种描述),对于底层(传输层)不管是grpc还是netty对于protobuf的支持,其实底层都是采用
 * netty进行数据传递
 *
 */