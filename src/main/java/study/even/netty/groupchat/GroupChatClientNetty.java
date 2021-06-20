package study.even.netty.groupchat;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class GroupChatClientNetty {
    private static final String HOST = "127.0.0.1";
    private static final String PORT = "8888";

    public void run() throws Exception{
        EventLoopGroup eventExecutors = new NioEventLoopGroup();

        try{
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventExecutors)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 得到pipeline
                            ChannelPipeline pipeline = ch.pipeline();
                            // 加入相关handler
                            pipeline.addLast("decoder", new StringDecoder());
                            pipeline.addLast("encoder", new StringEncoder());
                            // 加入自定义handler
                            pipeline.addLast(new GroupChatClientHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect(HOST, Integer.valueOf(PORT)).sync();

            //得到channel
            Channel channel = channelFuture.channel();
            // 客户端需要输入信息，创建一个扫描器
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNextLine()){
                String nextLine = scanner.nextLine();
                // 通过channel发送消息到服务器
                channel.writeAndFlush(nextLine);
            }
        }finally {
            eventExecutors.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception{
        new GroupChatClientNetty().run();
    }

}
