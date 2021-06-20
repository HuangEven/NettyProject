package study.even.netty.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.time.LocalDateTime;

public class MyTextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    /**
     * @param ctx
     * @param msg 表示一个文本帧（frame）
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println("服务器收到消息：" + msg.text());

        // 回复消息
        ctx.channel().writeAndFlush(new TextWebSocketFrame("服务器时间" + LocalDateTime.now() + msg.text()));


    }

    /**
     * web客户端连接后，触发该方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // id表示唯一的值，asLongText唯一
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asLongText());
        // asShortText值可能不唯一
        System.out.println("handlerAdded 被调用" + ctx.channel().id().asShortText());

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        System.out.println("异常发生：" + cause.getMessage());
        // 关闭连接
        ctx.close();
    }
}
