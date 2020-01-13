package com.hhy.thrift;

import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.THsHaServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import thrift.generated.PersonService;

public class ThriftServer {

    public static void main(String[] args) throws Exception{
        new ThriftServer().start();
    }

    private void start() throws Exception{
        TNonblockingServerSocket socket = new TNonblockingServerSocket(8899);
        THsHaServer.Args arg = new THsHaServer.Args(socket).minWorkerThreads(2).maxWorkerThreads(4);
        PersonService.Processor<PersonServiceImpl> processor = new PersonService.Processor<>(new PersonServiceImpl());
        //processor 处理器
        arg.protocolFactory(new TCompactProtocol.Factory());//协议层 高层 压缩的工厂 (协议可自己指定)
        arg.transportFactory(new TFramedTransport.Factory());//传输层 底层 (底层以什么形式从一端传递到另一端)
        arg.processorFactory(new TProcessorFactory(processor));

        TServer server = new THsHaServer(arg);//THsHaServer  HsHa = half synchronize half asynchronous 半同步半异步

        System.out.println("Thrift Server Started!");

        server.serve();//死循环 异步非阻塞 永远不会退出
    }


}
