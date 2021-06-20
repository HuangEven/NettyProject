package study.even.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class MyServer {
    private static final String IN_NET_PORT = "8888";

    /**
     * 当服务器超过3s没有读时，就提示读空闲
     * 当服务器超过5s没有写操作时，就提示写空闲
     * 当服务器超过7s没有读或者写操作时，就提示读写空闲
     *
     * @throws Exception
     */
    public void run() throws Exception{
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) // bossGroup增加一个日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            // 加入一个Netty提供的IdleStateHandler
                            /*
                            说明：
                            1、IdleStateHandler是Netty提供的处理空闲状态的处理器
                            2、readerIdleTime, 表示多长时间没有读，就会发送一个心跳检测包检测是否连接
                            3、writerIdleTime, 表示多长时间没有写，.....
                            4、allIdleTime, 表示多长时间没有读写，.....
                            5、文档说明
                            triggers an {@link IdleStateEvent} when a {@link Channel} has not performed read,write.or both operation for a while
                            6、当触发IdleStateEvent 后，就会传递给管道的下一个handler去处理，
                            通过调用（触发）下一个handler的 userEventTriggered方法，
                            在该方法中去处理IdleStateEvent
                             */
                            channelPipeline.addLast(new IdleStateHandler(3, 5, 7, TimeUnit.SECONDS));
                            // 加入一个对空闲检测进一步处理的handler（自定义）
                            channelPipeline.addLast(new MyServerHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(Integer.valueOf(IN_NET_PORT)).sync();

            channelFuture.channel().closeFuture().sync();

        }finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        new MyServer().run();
    }
}
