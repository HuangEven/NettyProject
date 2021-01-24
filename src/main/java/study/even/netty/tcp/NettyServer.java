package study.even.netty.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Even
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {
        //创建BossGroup和WorkerGroup
        /**
         * 1、创建线程组bossGroup和workerGroup
         * 2、bossGroup只是处理连接请求，真正的和客户端业务处理交给workerGroup完成
         * 3、两个线程组都是无限循环
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //创建服务器端的启动对象，配置参数
        ServerBootstrap bootstrap = new ServerBootstrap();

        //使用链式变成来进行设置
        bootstrap.group(bossGroup,workerGroup)  //设置两个线程组
                .channel(NioServerSocketChannel.class)  //使用NioSocketChannel作为服务器的通道实现
                .option(ChannelOption.SO_BACKLOG, 128)  //设置线程队列得到的连接个数
                .childOption(ChannelOption.SO_KEEPALIVE, true)  //设置保持活动连接状态
                .childHandler(new ChannelInitializer<SocketChannel>() { //创建一个通道测试对象（匿名对象）
                    //给pipeline设置处理器
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                }); //给workerGroup的EventLoop对应的管道设置处理器

        System.out.println("...........Server is ready..........");

        //绑定一个端口并且同步，生成了一个ChannelFuture对象
        //启动服务器（并绑定端口）
        ChannelFuture channelFuture = bootstrap.bind(6668).sync();

        //对关闭通道进行监听
        channelFuture.channel().closeFuture().sync();

    }
}
