package study.even.netty.groupchat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class GroupChatServerNetty {
    private static final String IN_NET_PORT = "8888";

    /**
     * 处理客户端请求
     */
    public void run() throws Exception {
        EventLoopGroup boosGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();

            serverBootstrap.group(boosGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 获取到pipeline
                            ChannelPipeline channelPipeline = ch.pipeline();
                            // 向pipeline加入解码器
                            channelPipeline.addLast("decoder", new StringDecoder());
                            // 向pipeline加入编码器
                            channelPipeline.addLast("encoder", new StringEncoder());
                            // 加入业务处理handler
                            channelPipeline.addLast(new GroupChatServerHandler());
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(Integer.valueOf(IN_NET_PORT)).sync();
            // 监听关闭
            channelFuture.channel().closeFuture().sync();
        }finally {
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception{
        new GroupChatServerNetty().run();
    }
}
