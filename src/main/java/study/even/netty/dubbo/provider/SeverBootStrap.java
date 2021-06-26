package study.even.netty.dubbo.provider;

import study.even.netty.dubbo.netty.NettyServer;

public class SeverBootStrap {
    public static void main(String[] args) throws Exception{
        NettyServer.start("127.0.0.1", "8888");
    }
}
