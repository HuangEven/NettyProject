package study.even.netty.dubbo.consumer;

import study.even.netty.dubbo.netty.NettyClient;
import study.even.netty.dubbo.public_interface.HelloService;

public class ClientBootStrap {
    /**
     * 这里定义协议头
     */
    public static final String header = "hello_world#";

    public static void main(String[] args) throws Exception{
        // 创建一个消费者
        NettyClient nettyClient = new NettyClient();

        // 创建代理对象
        HelloService helloService = (HelloService) nettyClient.getBean(HelloService.class, header);

        // 通过代理对象调用服务提供者的方法（服务）
        String result = helloService.hello("hello, dubbo~");
        System.out.println("调用的结果："+result);

    }

}
