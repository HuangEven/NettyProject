package study.even.netty.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;


public class TestServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        /*向管道加入处理器*/
        // 得到管道
        ChannelPipeline channelPipeline = ch.pipeline();
        // 加入一个Netty提供的httpServerCodec【codec —— {coder - decoder}】
        // 1、HttpServerCodec 是Netty提供的处理http的 编码-解码器
        channelPipeline.addLast("MyHttpServerCodec", new HttpServerCodec());
        // 2、增加一个自定义的handler
        channelPipeline.addLast("MyServerInitializer", new TestHttpServerHandler());
    }
}
