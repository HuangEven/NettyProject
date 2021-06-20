package study.even.netty.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {
    private static final String IN_NET_PORT = "8888";

    public void run() throws Exception{
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO)) // bossGroup增加一个日志处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();

                            // 因为基于http协议，使用http的编码和解码器
                            channelPipeline.addLast(new HttpServerCodec());
                            // 是以块方式写的，添加相应处理器
                            channelPipeline.addLast(new ChunkedWriteHandler());
                            /*
                            说明：
                            1、http数据在传输过程中分段，HttpObjectAggregator可以将多个段聚合
                            2、这就是为什么，当浏览器发生大量数据时，就会发出多次http请求
                             */
                            channelPipeline.addLast(new HttpObjectAggregator(8192));
                            /*
                            说明：
                            1、对应webSocket，它的数据是以帧（frame）的形式传递
                            2、webSocketFrame下面有6个子类
                            3、浏览器请求时ws://localhost:8888/hello 表示请求的uri
                            4、WebSocketServerProtocolHandler核心功能是将http协议升级为ws协议，保持长连接
                            5、是通过一个状态码101
                             */
                            channelPipeline.addLast(new WebSocketServerProtocolHandler("/hello"));
                            // 自定义TextWebSocketFrameHandler
                            channelPipeline.addLast(new MyTextWebSocketFrameHandler());
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
        new WebSocketServer().run();
    }
}
