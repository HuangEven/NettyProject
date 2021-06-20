package study.even.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

    // 定义一个channel组，管理所有的channel
    // GlobalEventExecutor.INSTANCE——全局事件执行器，是一个单例
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 表示连接建立，一旦连接，触发此方法，第一个被执行
     * 在此方法中将新建channel 加入到channelGroup
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        // 将该客户加入聊天室的信息推送给其他在线的客户端
        /*
        该方法会将channelGroup中所有的channel遍历，并发送消息
        不需要另外遍历
         */
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + " 加入聊天\n");
        // 先通知其他客户端有新客户上线，再将该新客户加入channelGroup
        channelGroup.add(channel);
    }

    /**
     * 表示连接断开，
     * 断开连接，将当前客户端离开信息推送给其他客户端
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channel.writeAndFlush("[客户端]" + channel.remoteAddress() + " 离开了\n");
        // 当前channel将自动从channelGroup移除，不需要再另外处理
    }

    /**
     * 表示channel 处于活动状态，可用于做提示客户上线操作（通知服务器）
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "上线了。");
    }

    /**
     * 表示channel 处于不活动状态，可用于做提示客户离线操作（通知服务器）
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(ctx.channel().remoteAddress() + "离线了。");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        // 获取当前channel
        final Channel channel = ctx.channel();

        // 针对不同的客户端回送不同消息（自己就不需要收到自己的信息了）
        channelGroup.forEach(ch -> {
            if (channel != ch) {
                // 不是当前channel，转发消息
                ch.writeAndFlush("[客户]" + channel.remoteAddress() + "发送了消息：" + msg + "\n");
            }else{
                // 回显还是怎么处理再说
            }
        });
    }
}
