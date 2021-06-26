package study.even.netty.dubbo.provider;

import study.even.netty.dubbo.public_interface.HelloService;

public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String msg) {
        System.out.println("收到客户端的信息：" + msg);
        if (msg != null) {
            return "你好，客户端，收到了你的信息：[" + msg + "]";
        } else {
            return "你好，客户端，没有收到你的信息。";
        }
    }
}
