package study.even.netty.dubbo.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NettyClient {

    private static final String IN_NET_HOST ="127.0.0.1";
    private static final String PORT = "8888";

    private static ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    private static NettyClientHandler clientHandler;

    public Object getBean(final Class<?> serviceClass, final String header) throws Exception{
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class<?>[]{serviceClass}, (proxy, method, args)->{
                    // 客户端每调用一次hello，就会进到这部分代码
                    if (clientHandler==null) {
                        start(IN_NET_HOST, PORT);
                    }

                    // 设置要发给服务端的信息
                    // 加上协议头
                    clientHandler.setPara(header + args[0]);

                    return executor.submit(clientHandler).get();
                });
    }


    public static void start(String inNetHost, String port) throws Exception{
        clientHandler = new NettyClientHandler();

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(clientHandler);
                    }
                });

        bootstrap.connect(inNetHost, Integer.valueOf(port)).sync();


    }

}
