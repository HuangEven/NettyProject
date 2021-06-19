package study.even.netty.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Created by Even
 * Date: 2021-01-24
 */

/**
 * ChannelInboundHandlerAdapter入栈handler适配器
 * 1、自定义一个Handler，需要继承netty规定好的某个HandlerAdapter（有相关规范规定）
 * 2、这时自定义handler才能起作用
 */
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * 读取数据实际（这里可以读取客户端发送的信息）
     * @param ctx   上下文对象，含有管道pipeline（可以关联很多handler，进行业务逻辑处理），通道channel（关注数据，处理数据读写），地址
     * @param msg   客户端发送的数据，以对象形式传输，默认Object
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server context: "+ctx);
        /**
         * 将msg转成一个ByteBuff
         * ByteBuff是Netty提供的，与NIO的字节缓冲ByteBuffer不同
         */
        ByteBuf byteBuf = (ByteBuf) msg;
        System.out.println("客户端发送消息："+byteBuf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址："+ctx.channel().remoteAddress());

    }

    /**
     * 读取数据完毕
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        /**
         * writeAndFlush = write + flush
         * 将数据写入缓存并刷新
         * 一般需要对发送数据进行编码
         */
        ctx.writeAndFlush(Unpooled.copiedBuffer("你好，客户端~",CharsetUtil.UTF_8));

    }

    /**
     * 处理异常，一般需要关闭通道
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
    }
}
