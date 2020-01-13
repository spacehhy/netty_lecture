package com.hhy.nio;


/**
 * java.io
 * java.nio
 *
 * java.io  随着jdk最初的版本发布而出现,以阻塞的方式的方式来处理输入,输出的
 * java.nio 随着jdk1.4版本发布而出现,以非阻塞方式处理io操作(non-blocking io/new io)
 *
 * java.io中最核心的一个概念是流(stream),面向流的编程。流实际代表的是信息的载体,比如FileInputStream文件当中的数据会以字节的形式在外界源源不断的流到程序当中
 * 所以我们程序当中是通过流来去获取到里面相关的字节信息,所以IO当中最为核心的概念就是流。而流又分为两种，一种是输入流，一种是输出流；Java中，一个流要么是输入流
 * 要么是输出流，不可能同时既是输入流又是输出流。
 * java.nio中拥有3个核心概念: Selector(选择器),Channel(通道)与Buffer(缓冲区)。在java.nio中,我们面向块(block)或是缓冲区(buffer)编程的。
 *              javaNIO核心概念图
 *                      ()  Selector <--- Thread
 *                /     |      \
 *               /      |       \
 *              /       |        \
 *            []       []        []   Channel
 *            |        |         |
 *           {}       {}        {}    Buffer
 *
 * Channel一定存在与之对应的buffer,例如: 假设有一个线程,对应一个Selector;而这个Selector对应三个Channel,每个Channel对应一个buffer,一个线程可以在三个通道上
 * 来回的切换,具体某一时刻切换到哪,实际上是通过事件来决定的,在NIO编程当中event事件是一件特别重要的概念,(类似于客户端浏览器端通过ajax编程时,就是通过事件来判断的,
 * 什么时候连接建立了,什么时候数据发送成功了,什么时候服务器返回了数据了,什么时候数据返回完了,什么时候数据处理完了,一个一个的事情都是通过事件决定的,所以程序编写
 * 当中就是通过事件判断来去进行相应的响应)。在NIO编程当中,与这个是非常类似的,比如说第一个Channel,它有数据想要写到缓冲区当中,并且这个事件发生了,那么Selector就
 * 会选择Channel1来处理,如果第一个Channel处理完成之后,可能正在处理当中,那么这个Selector还可以去切换到其他Channel。可以将NIO中的Channel理解成IO当中的Stream
 * ,虽然不是特别特别的一致,但是这样理解是没有什么问题的。其他的类、方法、接口都是围绕着这三个核心组件进行设置的。
 * Buffer本身就是一块内存,底层实现上,它实际上是个数组。数据的读、写都是通过Buffer来实现的。buffer内存的大小是通过buffer对应的allocate方法来指定的。
 *
 * 除了数组之外,Buffer还提供了对于数据的结构化访问方式,并且可以追踪到系统的读写过程。
 *
 * Java中的7种原生数据类型都有各自对应的Buffer类型,如IntBuffer,LongBuffer,ByteBuffer,CharBuffer等等。并没有BooleanBuffer类型。
 * Channel ≈ stream
 * Buffer ≈
 * 在IO当中 Stream本身是一个流,比如输入流,数据是从流当中读到我们程序当中的,读取的方式就是读,我们从Stream中读到一个字节,就拿到程序当中。数据就直接从stream就读取到程序里
 * 在NIO当中是绝对绝对不会出现这种情况的,在NIO当中buffer是一个极为重要的概念,数据从哪来？比如读,读的数据是来自于Channel的,(比如FileChannel文件通道里读,文件通道最后会
 * 连接到磁盘中某个特定的文件),我们从文件当中读取到程序当中,绝对不是从Channel当中把文件拿到了,而是首先一定要执行一个步骤,就是把数据从Channel当中读取到Buffer当中,数据进
 * 到内存当中了(底层数组当中了),进到数组之后,紧接着程序在去读取数组当中的内容.（数据一定是从Channel到Buffer再到程序当中，一定不会从Channel直接读取到程序当中）这是第一点
 * 第二点，与IO不同在于IO里面一个流不可能同时既是输入流、又是输出流,而NIO是存在这种情况的,数据从一个Channel中读到Buffer当中,那么程序就可以通过buffer把数据读取到程序当中
 * 读取完之后,我还可以将程序写回到buffer当中,话句话说,buffer不仅可以读,还可以写,这种状态其实就是通过flip()方法进行实现的,flip()方法:实现读写切换。
 *
 * Channel指的是可以向其写入数据或是从中读取数据的对象,它类似于java.io中的Stream
 *
 * 所有数据的读写都是通过Buffer来进行的,永远不会出现直接向Channel写入数据的情况,或是直接从Channel读取数据的情况。
 * 与Stream不同的是,Channel是双向的,一个流只可能是InputStream或是OutputStream,Channel打开后则可以进行读取、写入或是读写。
 * 由于Channel是双向的,因此它能更好地反映出底层操作系统的真实情况;在Linux系统中,底层操作系统的通道就是双向的。
 *
 * 关于NIO Buffer中3个重要状态属性的含义: position limit capacity (位置 限制 容量)
 * position的含义是将要读或是写的元素的索引
 *
 *    []        []      []      []      []      []      {虚拟的元素}
 *                                                      capacity
 *                                                      limit
 * position
 *
 * 当buffer创建好之后 buffer的 capacity值为6;limit值为6;position值为0;
 * capacity值不会进行变化,后续读写操作,改变的是 limit和position的值
 *
 * 当读进两个元素position的位置移动到 索引2位置
 *    [1]        [2]      []      []      []      []      {虚拟的元素}
 *                                                      capacity
 *                                                      limit
 *                   position
 *
 * 在读进两个元素position的位置指向 索引4位置
 *    [1]        [2]      [3]      [4]      []      []      {虚拟的元素}
 *                                                      capacity
 *                                                      limit
 *                                    position
 *
 * 然后开始进行读操作,先调用flip()方法反转
 * public final Buffer flip() {
 *     limit = position;
 *     position = 0;
 *     mark = -1;
 *     return this;
 * }
 * 第一件事情现将position指向位置赋给limit (因为position之后的位置,没有存放元素)
 * 第二件事情现将position从任意位置指向索引0 (让进行读操作时,从第一个元素开始读取)
 *
 * 当写出两个元素时 limit索引不改变 position索引位置改变 (索引2)
 *    []        []      [3]      [4]      []      []      {虚拟的元素}
 *                                                      capacity
 *                                       limit
 *                   position
 *
 * 当再写出两个元素时 limit索引不改变 position索引位置改变 (索引4)
 *    []        []      []      []      []      []      {虚拟的元素}
 *                                                      capacity
 *                                    limit
 *                                   position
 * Buffer源码解读:
 * 因此 0<=mark<=position<=limit<=capacity
 * direct buffers:直接缓冲
 * heap buffers:间接缓冲
 *
 *  文件通道用法详解课程总结:
 *  通过NIO读取文件涉及到3个步骤:
 *  1.从FileInputStream获取到FileChannel对象。
 *  2.创建Buffer。
 *  3.将数据从Channel读取到Buffer中。
 *
 *  绝对方法与相对方法的含义:
 *  1.相对方法:limit值和position值会在操作时被考虑到。
 *  2.绝对方法:完全忽略掉limit值与position值。*
 *
 *  NIO堆外内存与零拷贝:
 *  从allocateDirect -> DirectByteBuffer(直接缓冲) ->
 *  Buffer类(最高父类)中定义了address属性: 本来应该是在direct buffers中定义的成员变量,
 *  但是为了提高JNI获取direct buffers地址时提升调用速度
 *  // Used only by direct buffers
 *  // NOTE: hoisted here for speed in JNI GetDirectBufferAddress
 *  long address;
 *
 *  java:    DirectByteBuffer                    (堆内存)
 *                                address[C/C++堆外内存地址]
 *  -------------------------------------------  (堆)
 *  native:  malloc                 |            (堆外内存)
 *                                  |
 *                                 数据
 *
 *  将数据防止在堆外内存当中,是出于对效率角度考虑所做出的设置,
 *  HeapByteBuffer:间接缓冲数据读取或写入时。封装的内存包括字节数组都是在java堆上面的。然而对于操作系统来说,进行IO操作,操作形态并
 *                 不是直接就处理HeapByteBuffer,在堆上封装的字节数组,(并不是直接操作字节数组)他会在操作系统,java内存模型外面又会
 *                 开辟一块内存区域,比如进行数据的写入,实际上是将java堆上HeapByteBuffer里面的字节数组内容拷贝到java模型之外开辟的
 *                 内存空间里面的某一区域,然后再把数据拿出来,与IO设备进行直接的数据读取或写入;使用HeapByteBuffer间接缓冲在真正操作
 *                 数据时实际是多了一次数据拷贝的过程,会把java内存空间当中的字节数组内容原封不动的拷贝到java内存模型之外的操作系统
 *                 的某一块内存当中,然后这块内存区域会直接与我们的IO设备进行交互。
 *  DirectByteBuffer:在使用DirectByteBuffer直接缓冲时,在java堆上面就不会再存在一个字节数组,因为真正的数据就在堆外存放,因此如果进行
 *                   数据读写,直接由堆外的操作系统与堆外内存进行交互,少了一次数据拷贝的过程,这种方式有一种称谓叫做Zero copy零拷贝。
 *  如果使用HeapByteBuffer,它里面的数组就是维护在java堆上面的,为什么操作系统不会直接操作堆上面的数据?并不是操作系统没办法访问堆上内存
 *  操作系统在内核态场景下是可以访问任何一块内存区域的,操作系统是一定可以访问到这块内存区域的,真正的操作系统访问,进行IO操作,一定都是和
 *  外设打交道的,一定会调用操作系统的实现,这就相当于是通过JNI的方式来去访问这块内存区域,通过JNI的方式来访问内存区域,这块地址一定是已经
 *  确定的才能访问到这块内存区域,然而你正在访问这块内存区域时,突然在这块内存区域发生了GC垃圾回收,(垃圾回收有多种的垃圾回收算法,除了CMS
 *  就是并发的标记清除算法之外,那么其他的垃圾回收方式都涉及到,先标记然后再去压缩过程)。先标记再压缩过程：
 *  打叉表示需要回收掉，没有打叉表示继续保留
 *  【x】【】【】【x】【】【】【x】【】
 *  它把三个打叉内存清空之后,它会执行一次压缩,所谓压缩就涉及到对象的移动,移动的目的是为了腾出一块更大的完整的连续的内存空间,让内存可以
 *  容纳更大的java对象,如果native正在操作内存数组时,突然发生了数据移动的话,那么整个数据就乱套了,所以不能进行GC这种操作,GC不能操作,很
 *  可能出现OutOfMemoryError扎样的错误,因此有两种办法,
 *  第一种是让native的本地的方法来操作时,让对象固定在那,不要发生移动。让对象不去移动,对于一些VM来说,他也有一些考量,是不太现实的,单个
 *         对象不去移动式不太现实的,
 *  第二种是不要让它发生垃圾回收,这种方案是显然不可行的,这种方式就堵死了,就是说只能通过另外一种方式,就是说把字节数组的内存拷贝到堆外上
 *         去,拷贝到堆外上去是基于这样的条件,拷贝动作是比较快的,同时IO操作速度又没那么快,所以这种操作性价比是比较高的事情,所以执行这
 *         么一次拷贝的过程。那么这个拷贝就涉及到了JNI的相应拷贝内存方法的调用,那么在拷贝的时候实际上也不会产生GC的,如果在拷贝过程当中
 *         也产生GC的话,那么实际上也是没有意义的,内存也会发生变化的,这一点呢JVM会做出一次保证,就会完成一次数据的拷贝过程,数据从堆上内存
 *         拷贝到操作系统所分配的内存空间上之后,那这个事情操作就变得简单了,内存就由操作系统来把控,来进行相应的IO操作,堆外内存由操作系统
 *         维护,用完之后就被释放掉了。
 *
 * 对于零拷贝,数据是如何释放的?因为address维护着堆外内存的引用,相当于它的一个地址,也就是java堆上的DirectByteBuffer对象被回收掉之后,它
 * 就能找到相应的堆外内存,接下来就通过JNI的方式,就可以把堆外内存给回收掉,因此依然不会出现内存泄露的情况。
 * netty里面directBuf类.它就是用到了零拷贝概念,减少了一次内存拷贝过程,从而大大提升了IO的效率和速度
 *
 * DirectByteBuffer是存储在java堆上标准的java对象,但是它持有一个操作系统内存,即java内存之外的一个内存的引用,指向了这个内存,称之为直接
 * 内存模型,在直接内存模型之前,java堆上面的数据,想要与外界进行交换的话,比如IO设备,外界设备值非jvm内存模型的领域,必须要经过native堆,这样
 * 就需要实现数据从java堆拷贝到native堆的过程,由native进行相关操作,实际上多了一次没有意义的拷贝;如果使用堆外内存或直接内存的情况,直接放
 * 入操作系统的内存当中,就可以直接跟IO打交道,
 * ByteBuffer有两种实现类,一种是间接缓冲,
 * 一种是直接缓冲,java虚拟机可以直接的进行本地IO操作,避免了在操作系统的原生IO操作还需要复制内容到中间的一个缓冲区
 *
 * MappedByteBuffer: 是一个直接的缓冲区,他的内容是一个文件的内存映射区域,MappedByteBuffer可以通过FileChannel的map方法来去实现,一个
 * mapped byte buffer以及他所表示的文件映射会保持一致有效直到buffer本身被垃圾回收掉,内存映射文件表示,文件本身把它的内容映射到内存里面,
 * 我们只需要在内存当中去操作相关的信息,最终信息会写到文件当中,换句话说我们不需要直接跟磁盘上文件打交道,只需要跟内存打交道,那么我们对内存
 * 的任何修改都会写到磁盘上。内存映射文件时一种允许java程序直接从内存访问的一种特殊文件,我们可以将整个文件,或者整个文件的一部分映射到内存
 * 当中接下来由操作系统完成相关的页面请求并且将内存修改写入到文件当中,应用程序只需要处理内存的数据,可以实现非常迅速的IO操作,用于内存映射
 * 的内存本身是在java堆的外面,换句话说是堆外内存。
 *
 * 关于Buffer的Scattering(分散)和Gathering(收集):
 * Scattering一个分散成多个,Gathering多个汇聚成一个,之前所有的关于buffer的所有的读、写,如果是read的话,read本身就接收一个byteBuffer对象
 * 写或读都是把信息放置到传递进去的buffer当中,buffer只有个一个,最多只能装那么多,装满之后在去重新定位position位置,再去重新写/读,写也是一样
 * 的只不过过程是反过来的
 * Scattering是在我们读的时候不仅可以传递一个buffer,可以传递一个buffer数组,比如从channel中将数据读到buffer里面,channel里面有20个字节,
 * 可以传递一个buffer数组,然后往数组里去读信息,第一个数组的capacity的长度为10,第二个是5,第三个也是5,那么它会将第一个buffer读满,满了之后再
 * 往第二个buffer里面读5个,如果在读满了再往第三个buffer里读5个,将一个来自channel的数据读到多个buffer当中,按照顺序,只有把第一个读满再去读
 * 第二个,读满再去读第三个,如果第一个没有读满,不会去读第二个。
 * Gathering是当我们写的时候也可以传递一个buffer数组,他会将第一个数组中的数据全部写到channel当中,然后接着写第二个,第三个,按照顺序来进行
 * Scattering和Gathering的用途、场景:比如进行网络操作时,自定义了协议,比如第一个头10个字节,第二个header长度5字节,第三个body消息体,长度是
 * 可变的,在这种情况下就可以使用Buffer的Scattering在读的时候,就把第一个header的10个字节读到第一个buffer当中,将第二个header的字节读到第二
 * 个buffer当中,将body读到第三个buffer当中,这样天然的实现了一种数据的分门别类,而不必只传递一个buffer,把头和体的信息都写到buffer当中,然后
 * 再去解析buffer。Gathering和他类似的。
 * demo是一个网络程序,需要一个客户端和一个服务器端;服务端是java代码中编写,客户端可以采用一些工具,如:
 * telnet https://www.cnblogs.com/Anidot/articles/6875219.html
 * nc(netcat) https://www.cnblogs.com/CYHISTW/p/11302382.html
 * mac netsat
 *
 * selector
 *
 *
 */


