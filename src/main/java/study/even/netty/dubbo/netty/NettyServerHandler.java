package study.even.netty.dubbo.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import study.even.netty.dubbo.provider.HelloServiceImpl;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 获取客户端发送的消息，并调用服务
        System.out.println("msg:"+msg);
        // 客户端在调用服务器的api时，我们需要定义一个协议
        // 比如要求每次发消息都必须以某个字符串开头：“hello_world#”
        String msgStr = msg.toString();
        String realMsg = new String();
        if (msg.toString().startsWith("hello_world#")) {
            realMsg = msgStr.substring(msgStr.indexOf("#")+1);
            new HelloServiceImpl().hello(realMsg);
        }

        ctx.writeAndFlush(realMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
