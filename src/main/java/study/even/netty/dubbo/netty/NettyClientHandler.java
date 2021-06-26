package study.even.netty.dubbo.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.Callable;

public class NettyClientHandler extends ChannelInboundHandlerAdapter implements Callable {

    /**
     * 上下文
     */
    private ChannelHandlerContext channelHandlerContext;
    /**
     * 返回的结果
     */
    private String result;

    public void setPara(String para) {
        this.para = para;
    }

    /**
     * 客户端调用方法时，传入的参数
     */
    private String para;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        channelHandlerContext = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        result=msg.toString();
        // 唤醒等待的线程
        notify();
    }


    /**
     * 被代理对象调用，发送数据给服务器，
     * -》wait （结果给到channelRead）等待被唤醒
     * @return
     * @throws Exception
     */
    @Override
    public synchronized Object call() throws Exception {
        channelHandlerContext.writeAndFlush(para);
        // 等待channelRead读取服务端信息，再唤醒
        wait();
        return result;
    }
}
