package com.hhy.protobuf;

/**
 * Google ProtoBuf 使用方式：
 * Google ProtoBuf 全称 Protocol buffers 作用主要是用来进行RPC的数据传输，换句话说是自定协议可好的，体积更小的方式，对数据进行编码并且可以解码，实际上
 * 就是序列化与反序列化过程，是RPC开发过程中，很常用的一个库（框架）跟Apache Thrift属于同一个领域的技术,Google Protobuf出现的比较早了,Google既Protobuf
 * 之后又推出了gRPC 用于序列化的第三方的库,其实这些库的原理大同小异,具体使用方式略有差别,包括语言的支持数量与支持力度上,存在一些差异,
 * 使用原理:
 * RPC 提到RPC我们就不得不想到在java里面一个比较成熟同时是EJB标准所大量使用的技术 RMI
 * rmi: remote method invocation  远程方法调用  只针对java    EJB???  https://blog.csdn.net/u011038738/article/details/79414256
 * 远程方法调用:有两台机器A和B;在A机器调用一个方法,而这个对象位于B这台机器上,从使用者角度是感知不到这一点的,从使用者角度来看就好像调用了A机器上已经存在的一个
 * 对象的一个特定的方法一样,写法上都是一样的,但实际上这个方法是在B这台机器上调用的,本质上是跨机器的方式
 * 既然是跨机器,肯定不是像传统的在本地的一台机器上直接调用的形式,那既然是跨机器的,一定是通过一种网络传输,比如说,在A机器上通过网络传输的形式,把调用的对象,调用
 * 的方法,包括传递的参数等等都给序列化字节码形式,然后通过网络的形式传输到B机器上,B机器将接受到的一系列字节码又给它转换成,还原成B机器特定方法的调用,参数也一并
 * 传进去,实际上是采用这样一种方式,既然这样那么存在两个概念
 * client  stub  装
 * client调用server的特定方法,具体调用传输的对象,传输的方法,包括参数都是以字节码的形式给序列化成字节码,然后通过网络形式传输给server机器上
 *
 * server  skeleton  骨架
 * 传输到server机器上,server进行反序列化操作,还原成真正的这台机器上特定的对象的特定的方法调用,把返回结果,以字节码形式,通过网络返回给clint端
 * client再反序列化成,自己可以理解的真正结果。
 *
 * RMI 有一个极大的限制,只针对java client和server 都必须是java代码 限制了它的应用 EJB本身是没问题的,基于java编写,底层大量的使用RMI
 * 客户端和服务端既然要通过网络传输这些字节码信息,势必要有一些网络底层的细节,这些细节对开发者来说实际上是屏蔽掉的,开发者可以不关心这些具体的内容
 * 只需要按照正常的方式调用就可以了,那么这种机制一定有一种保障来去实现这种网络传输的自动化,实际上RMI存在一个代码生成的概念,实际上,绝大多数,几乎所有的RPC框架
 * 都存在代码生成概念,定义好一个规范(标准),自动生成代码,生成代码里面,去完成了实际的序列化和反序列化过程,以及网络传输的过程,这种自动生成的代码 client端叫做
 * stub(装) 客户端叫做 skeleton(骨架)  实际上是当调用时,客户端把数据传递给了 stub;stub和skeleton底层通过socket形式数据传输,skeleton收到数据后反序列
 * 化成server 能够理解的真正的方法调用,再进行实际调用,调用后skeleton把调用结果传递给stub;stub再把结果翻译成 client能够理解的结果
 *
 * 序列化与反序列化 也叫做: 编码与解码  (进行RPC或者RMI非常重要,非常基本的机制)
 * 序列化: 将对象转化为字节  encode 编码
 * 反序列化:将字节转化为对象 decode 解码
 *
 * RPC和RMI道理上是极其类似的
 * RPC:  Remote  Procedure Call  远程过程调用
 * 原理同RMI及其相似的,存在客户端和服务器端,底层是通过socket双向数据传递,相比RMI优势在于 很多RPC框架是跨语言的
 * 1.定义一个接口说明文件:描述了对象(结构体)、对象成员、接口方法等一系列信息   (Thrift 称之为结构体,ProtoBuf称之为Message消息)
 *   注意这是一个文本文件,是独立于语言的
 * 2.通过RPC框架所提供的编译器，将接口说明文件编译成具体语言文件
 * 3.在客户端与服务端分别引入RPC编译器所生成的文件，即可像调用本地方法一样调用远程方法
 *
 * RPC和传统的webservice或http调用 很像 广义上看webservice也可以认为是RPC的一种实现方式,但是呢webservice相比于RPC性能和效率低一些
 * 决定RPC框架的性能强与弱几个重要因素,
 * 1.编解码效率(100->10压缩比例大,网络传输的速度一定快) ,可以很快的速度将压缩过后的数据解码成原来样子,传统webservice数据量是比较大的,传输上影响效率
 *   特别是在一些任务关键的,性能关键的应用中,是值得考量的一个问题
 * 2.在真正的数据传递上(RPC通过socket传输的,webservice是基于http的;传输的效率一定是socket效率更高)
 * 因此,在一些任务关键的,性能关键的应用中,才用RPC方式,特别是分布式应用,基于微服务,后端系统存在大量的服务与服务之间的调用,都采用Http方式,性能都会有损耗
 * 调用多了,性能的损耗是非常可观的,一般来说,公司内网中,服务与服务调用,更推荐RPC方式进行,netty在这种场景下是非常非常适合的
 *
 * 以上RPC框架的特点,适用场景,适用的方式
 *
 * protocol buffers  https://developers.google.com/protocol-buffers/
 * http方式传输数据,数据可能是 json或xml 特别是xml冗余数据非常大
 * RPC 将数据压缩,提高传输效率
 *
 * download
 * https://github.com/protocolbuffers/protobuf
 * 编译器下载
 * https://github.com/protocolbuffers/protobuf/releases
 * 向下找,找到 protoc-3.8.0-win64.zip 下载 解压缩在protoc文件夹下
 * 配置环境变量 E:\protoc\bin
 * protoc -h 命令查看 protobuf编译器是否安装成功
 * protoc -version 命令查看 protobuf编译器版本
 *
 * Protobuf Runtime Installation  运行时安装,既使用哪种语言
 * https://github.com/protocolbuffers/protobuf
 * 选择java 点击进入
 * https://github.com/protocolbuffers/protobuf/tree/master/java
 *
 * Gradle
 * If you are using Gradle, add the following to your build.gradle file's dependencies:
 * compile 'com.google.protobuf:protobuf-java:3.8.0'
 *
 * Maven 详见文档介绍 https://github.com/protocolbuffers/protobuf/tree/master/java
 *
 * 前言 最好的学习方式,打开框架官方网站,通过笔记或者自己编写代码验证,切记勿要复制粘贴,还可以锻炼英文阅读能力
 *
 * 打开指南
 * https://developers.google.com/protocol-buffers/docs/tutorials
 * https://developers.google.com/protocol-buffers/docs/javatutorial
 *
 * 编写.proto文件
 * optimize_for = SPEED;
 * https://developers.google.com/protocol-buffers/docs/proto
 * optimize_for (file option): Can be set to SPEED, CODE_SIZE, or LITE_RUNTIME
 *
 * protobuf 编译器生成代码命令:
 *
 * protoc -h
 * Usage: protoc [OPTION] PROTO_FILES
 * 用法 protoc 选项 proto_files文件的所在位置
 *
 * protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/addressbook.proto
 *
 * protoc --java_out=src/main/java src/protobuf/Student.proto
 * 编写ProtoBuf测试类,进行测试序列化与反序列化,注意 message是只读的,builder才是可读写的
 * https://developers.google.com/protocol-buffers/docs/javatutorial
 * Parsing and Serialization
 * byte[] toByteArray();: serializes the message and returns a byte array containing its raw bytes.
 * static Person parseFrom(byte[] data);: parses a message from the given byte array.
 * void writeTo(OutputStream output);: serializes the message and writes it to an OutputStream.
 * static Person parseFrom(InputStream input);: reads and parses a message from an InputStream.
 *
 * https://developers.google.com/protocol-buffers/docs/proto
 * Scalar Value Types   .proto 字段类型 对应的 java类型
 *                              int32           int
 *
 *
 * netty protoBuf test
 * MyDataInfo.Person.getDefaultInstance()  这种写法就写死了 造成netty只能处理MyDataInfo下的Person类型
 * http编程与socket编程很显著的一个不同
 * http编程的话,路由的信息即url映射,已经定义好了,映射到一个一个方法上,直接就可以调用对应的方法
 * RPC编程的话,都在一个端口号上双向的进行数据传递,每一次传递的数据,可能都是不同类型的,都是在相同的连接上进行数据的传递,与http编程有明显的差别
 * 具体到demo,如果客户端有多个message,或者说整个结构里有多个message,怎么能在Initializer对它进行判断和处理呢?对于服务器端,如果处理好的话,
 * 服务器端的channelRead0可以很明确的辨别是person1,person2,person3还是person4,
 * 主要的解决方式有两种方式:
 * 官方example给出了第一种解决方案 这种解决方案实际是利用了自定义协议来处理,在传递一个消息的时候,会对消息的前几位(前两位),作为自定义的一个位置,
 * 比如说前两位是AB,当解码器进行解析的时候,解析出来前两位是AB,那么它就认为是一种消息类型,如果是CD,它就认为是第二种消息类型,如果是EF就认为是第
 * 三种消息类型,以此类推,这种解决方案,需要自己手动写解码器,集成netty所提供的内建的解码器,把自己相应的逻辑写出来了.这种解决方案相对来说是复杂一些的
 * 是没有使用protoBuf提供的特性,而完全是基于netty,对于自定义协议的一种很好的实现和支持.
 *
 * 第二种解决方式可读性和可扩展性更好一些
 * 回到protobuf的IDL(IDL是Interface description language的缩写，指接口描述语言)文件当中,实际是通过消息的定义方式来解决这个问题
 * p.s. 多单词字段 使用下划线连接(不采用驼峰原则) protobuf编译器生成java文件时会将其转化为驼峰原则的字段
 *      .proto文件定义类型 尽量不采用required,因为采用required后,后续不能够删除这个字段,因为之前的解析会报错!!! 官方推荐使用optional/repeat(List)
 *      注意字段名后的 数字(小于15压缩为1字节,repeat[list]尽量采用15以下数字),同一个代码块里的数字不可重复,切不可以与 required字段后的数字相同
 * 在.proto的IDL文件里,最外层就定义一个消息,定义了一个消息之后,通过枚举或者其他方式,来去决定此次传递的消息类型到底是什么类型,消息本身可以不用嵌套的方式
 * 可以并列的去定义,终归是有一个最外层的一种消息,最外层消息包含了所有可能出现的消息类型,不管是客户端向服务器端发送,还是反过来服务器端向客户端返回的,那么所有
 * 的消息类型最终都是嵌套在最外层的消息对象当中,每一次传递时,传递的是消息当中的一种,这个时候可以通过枚举的方式来进行判断,确定好枚举之后,紧接着我就传递适合这
 * 中枚举的消息类型,比如枚举是requestInfo,我就传递与requestInfo对应的message,如枚举是responseInfo,我就传递与responseInfo对应的消息,
 * oneof 的含义:
 * If you have a message with many optional fields and where at most one field will be set at the same time,
 * you can enforce this behavior and save memory by using the oneof feature.
 * Oneof fields are like optional fields except all the fields in a oneof share memory, and at most one field can be set at
 * the same time. Setting any member of the oneof automatically clears all the other members.
 * You can check which value in a oneof is set (if any) using a special case() or WhichOneof() method,
 * depending on your chosen language.
 *
 * 如果你有一个消息,它拥有很多可选的字段,而最多在同一时刻只有一个字段会被设值,强制的应用这种行为,用oneof特性节省内存空间,oneof字段就像optional字段一样只
 * 不过,在oneof中的所有字段会共享内存,而最多只有一个字段在在同一时刻会被设值,设值了oneof当中的任何一个成员会自动的清除所有的其他成员,你可以检查这种值,是不是
 * 设定了使用了case或者等等其他的方法
 *
 * 如果消息特别特别多,channelRead0里的判断就会特别多,本质上问题 没有办法因为消息的更多,而采取更加便捷的方式.
 * 为什么在netty在基于protoBuf的数据传递过程中,存在这样一种情况,因为netty本身与客户端,建立的是TCP连接,不管是客户端向服务器端,还是服务器端向客户端发送数据
 * 都是在一条连接之上进行的,因此一端向另外一端发送数据的时候,对端必须要通过一种方式能确定出来对方到底给我发的是什么样的一种数据类型,其实网络协议主要是解决这样
 * 的一种问题,因此,只能通过 复杂的 判断方式,判断对方发过来的到底是什么样的一种数据类型
 *
 * 与SpringMVC或Struts2进行对比
 * @GetMapping(...="/users/user/123")
 * @PostMapping(...="/users/user")
 * 为什么SpringMVC 路由很清晰,直观,自然 而netty却是一堆if,else?
 * DispatcherServlet 前端控制器  (客户端向服务端的所有请求, 都先经过DispatcherServlet控制器,然后分发给不同的Controller上)
 * SpringMVC在启动的时候,找到url跟方法映射关系,启动时检查每一个方法上注解,判断url映射跟每一个方法是不是对应上,然后将map映射保存起来了
 * 其实SpringMVC框架已经将映射关系做好,我们只需要将Controller方法补充即可,而netty是更偏向底层的网络框架,没有提供路由相关的东西,包括使用netty进行http开发
 * 路由的判断都需要自己去写,netty更关注底层,更关注网络传输.
 *
 * 因此使用protobuf进行多协议传输时,建议使用第二种方式来进行。
 *
 * 假设项目使用的版本控制工具是git,客户端和服务器端是在不同的机器上,而他们又会共享protobuf生成出来的java代码,怎么让客户端和服务器端以最佳的方式去引用IDL文
 * 件编译生成出来的java代码,class文件
 *
 * 不管是protobuf或thrift这种RPC框架,怎么基于Git共享生成出来的中间文件,工程上的最佳实践是什么样子的。(两种)
 * 共享中间文件:文件拷贝是可行,但不可取的一种方法;
 *
 * 使用Git作为版本控制系统:(两种)
 * git submodule:
 * serverProject 是一个Git工程且使用了protobuf,需要引入.proto生成的java代码,
 * git submodule :Git仓库里面的一个仓库
 * 1.将中间生成的protobuf代码(通过protoc编译器生成的java代码)又作为一个独立的一个Git项目  ProtoBuf-java:(独立)
 * 2.通过git submodule方式将 ProtoBuf-java工程引入到serverProject工程中(具体命令百度相关文档~~~)
 *  当需要操作外层工程时,只需要进入到外层特定目录,进行git add,git commit,git push或者git pull,git fetch,对里层git工程时没有影响的
 *  当IDL文件发生变化,需要重新编译,编译好之后生成新的代码,将它推送到远程的ProtoBuf-java仓库里去,紧接着回到serverProject工程里面,切(cd)到里面工程的目录
 *  执行git pull,就会将新的代码拉去到serverProject仓库里去,ProtoBuf生成代码将会被serverProject使用
 * 可能还有clientProject,它的做法和serverProject做法完全一致
 * 这是一个非常非常好的做法,总体来说,除了客户端项目,服务端项目,还需要一个中间项目专门存放生成的protoBuf代码,通过git submodule方式把中间项目引入到
 * serverProject项目和clientProject项目,一旦中间项目发生变化,serverProject/clientProject分别进入相应的submodule目录中,分别执行git pull,把最新
 * 代码拉去到serverProject和clientProject项目中
 * 对于data.proto文件又应该作为一个独立的仓库用来专门存放IDL文件
 *
 * serverProject
 *
 * ProtoBuf-java
 * data.proto
 *
 * clientProject
 *
 * git submodule  本质上是在外层仓库里又有一个子仓库,在使用git开发时,通常会采用分支,一般有三个约定俗成的分支(develop,test,product)
 * branch
 *      develop 开发分支,频繁变更
 *      test    测试分支,一般给产品经理和测试人员使用,
 *      master  生产分支,最终线上版本,把test,merge到master分支上,把master推送到远程,接着可能通过jenkins或者(runack?软耐克??)构建工具完成
 *      自动化构建,部署到远程生产服务器,严格的或者比较好的做法,test上的环境跟master上的环境应该是完全一致的,test从节约成本上配置可能会低一些,整个的技术
 *      设施包括环境跟master,生成环境分支一模一样的,这样才能最大程度上,测试没有问题,生产环境出现问题的可能性降到最低,有开发经验的人会了解,很多时候线上的问题
 *      并不是代码的问题,很多时候是环境的问题,包括配置的问题、操作系统设置的问题,包括一些外部环境,我们让test和master分支对应的环境保持完全一致,就可以在最大
 *      可能降低生产环境出错的可能性,比如,生产环境采用 Https加SSL证书形式,测试环境采用 Http形式,这就可能造成测试环境正常,生产环境就有可能出问题,开发环境
 *      要求就不需要完全一致(有可能开发环境的配置还不如test环境),也就是说生产环境用集群,测试环境也要使用集群,因为有些时候,单机没问题,集群就会有问题,开发环境
 *      可以不用集群,便于排查问题
 *
 *  回到当前问题,  serverProject 和 clientProject 都存在三个分支,这样包含在submodule中的中间仓库ProtoBuf-java也会有三个分支
 *  假如,在 serverProject项目的develop分支时,ProtoBuf-java项目此时也在develop分支,这时如果serverProject项目切换分支到test,ProtoBuf-java项目是
 *  不会跟着切换到test分支的,这时就需要手动将ProtoBuf-java项目切换到test分支,换句话说外层项目分支和里层项目分支他们的分支要保持完全一致,但很多时候开发人员
 *  就忘了这一点,外层切换了分支,里层却未切换分支,那这个时候他在推送的时候就会把分支的信息一并推送到远程,然后另外一个开发人员把它拉下拉之后,就会导致分支的对应关
 *  系错乱。第二个问题,有些场景可能在serverProject项目里面进入中间项目ProtoBuf-java项目,去修改中间项目,修改完成后将修改推送回ProtoBuf-java项目里面,这
 *  样使用submodule就会产生一些问题,换句话说这个问题产生的根源是A项目引入中间项目,在A项目里面修改了中间项目的文件,接下来将中间项目推回到远程分支,这种情况下
 *  使用git submodule会产生一些问题,因此不建议使用 git submodule方式解决的两个问题
 *  1.分支切换产生分支紊乱的问题;2.外层(引用中间项目的仓库)里面,修改了中间项目的代码,推回远程,中间项目别人拉取时就会产生一些问题.
 *
 *  git submodule 本身是比较笨重,git很早之前提出的一个特性,(一个项目引用另一个项目,并把另一个项目包含到当前项目里面)是这样的一个解决方案
 *  存在问题: serverProject 的环境 与 protobuf-java 的环境不同 需要手动切换 麻烦
 *
 *  git subtree 推荐这种方式 集成 serverProject  protobuf-java clientProject
 *  subtree 是继 submodule 之后git引入的另外的一个特性,想法其实与submodule类似也会有三个仓库,serverProject、 clientProject和中间仓库
 *  protobuf-java;这三个项目的作用跟使用submodule也是完全一致,使用git subtree 实际上也是将 protobuf-java代码 拉取到 serverProject项目中,需要
 *  注意的一点使用git subtree之后,即便你是将protobuf-java代码拉取到serverProject项目之后,这个serverProject项目也仅仅只是一个仓库,而不是两个仓库,
 *  这是与git submodule最大的不同,将protobuf-java代码拉取到serverProject项目/clientProject项目之中,仅仅是把 protobuf-java里面的代码和
 *  serverProject项目/clientProject项目代码做了一次合并,合并之前会产生一个新的提交(具体细节可以搜素一下相关subtree知识介绍),protobuf-java  merge
 *  到serverProject项目里之后,serverProject项目还是保持自己一个工程存在,可以对唯一的仓库执行正常的分支切换,等等,都不会对protobuf-java项目产生任何影响
 *  也不会出现外层仓库分支切换的时候,里面的仓库不会进行切换即 git submodule所产生的问题
 *
 *  git subtree 推荐解决 protobuf和thrift这种RPC框架所产生的中间代码的引用问题。
 *  也可以打成jar包，上传至nexus上，这个时候需要注意上传时修改jar依赖的版本号，项目中引用jar处也要修改相应的版本号，也不是最优解决方案
 *
 *  工作中没用上也要学Git,掌握原理,基本命令,重要命令,参数。
 *
 */