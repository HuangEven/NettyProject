package study.even.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author: Even
 * @date: 2021-01-24
 */
public class NettyClient {
    private final static String SERVER_HOST = "127.0.0.1";
    private final static String SERVER_PORT = "6668";

    public static void main(String[] args) throws InterruptedException {
        // 客户端需要一个事件循环组
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();

        try {
            // 创建客户端启动对象
            // 注意客户端使用的是Bootstrap，不同于服务器使用ServerBootstrap
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(eventExecutors) // 设置线程组
                    .channel(NioSocketChannel.class)    // 设置客户端通道的实现类（使用反射）
                    .handler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new NettyClientHandler()); // 加入自己的处理器
                        }
                    });
            System.out.println("client is ok.....");

            // 启动客户端去连接服务器
            // 关于channelFuture要分析，涉及到netty的异步模型
            ChannelFuture channelFuture = bootstrap.connect(SERVER_HOST, Integer.valueOf(SERVER_PORT)).sync();
            // 给关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            // 优雅关闭
            eventExecutors.shutdownGracefully();
        }
    }
}
