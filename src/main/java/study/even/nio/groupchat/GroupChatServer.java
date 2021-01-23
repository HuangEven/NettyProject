package study.even.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

public class GroupChatServer {
    /**
     * 定义属性
     */
    // 选择器
    private Selector selector;
    // 监听通道
    private ServerSocketChannel listenChannel;
    // 监听端口
    private static final int SERVER_PORT = 6667;

    //构造器初始化
    public GroupChatServer() {

        try {
            //得到选择器
            selector = Selector.open();
            //打开通道
            listenChannel = ServerSocketChannel.open();
            //绑定端口
            listenChannel.socket().bind(new InetSocketAddress(SERVER_PORT));
            //设置非阻塞模式
            listenChannel.configureBlocking(false);
            //将通道注册到selector
            listenChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void listen() {
        try {
            //循环处理
            while (true) {
                //监听有哪些事件发生，如不设置，将阻塞，
                int count = selector.select(2000);
                if (count > 0) {//有事件待处理
                    System.out.println("有事件待处理");
                    //遍历得到的selectionKey集合
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        //取出selectionKey
                        SelectionKey key = iterator.next();
                        //监听到accept
                        if (key.isAcceptable()) {
                            SocketChannel sc = listenChannel.accept();
                            //记得设置非阻塞
                            sc.configureBlocking(false);
                            //将sc注册到selector
                            sc.register(selector, SelectionKey.OP_READ);
                            //提示
                            System.out.println(sc.getRemoteAddress() + "上线");
                        }
                        //通道发送read事件，即通道是可读状态
                        if (key.isReadable()) {
                            //处理读
                            readData(key);
                        }
                    }

                    //当前的key删除，防止重复处理
                    iterator.remove();
                } else {
                    System.out.println("等待，，，，，");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 读取客户端消息
     *
     * @param key
     */
    public void readData(SelectionKey key) {
        //定义一个SocketChannel
        SocketChannel socketChannel = null;
        try {
            //获取关联的通道
            socketChannel = (SocketChannel) key.channel();
            //创建buffer
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int count = socketChannel.read(buffer);
            //根据count的值做处理
            if (count > 0) {
                //把缓存区的数据转换成字符串
                String msg = new String(buffer.array());
                //输出该消息
                System.out.println("from 客户端：" + msg);

                //向其他客户端转发消息
                sendMsgToOthers(msg, socketChannel);

            }


        } catch (Exception e) {
            e.printStackTrace();
            try {
                System.out.println(socketChannel.getRemoteAddress() + "离线了，，，，");
                //取消注册
                key.cancel();
                //关闭通道
                socketChannel.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * 转发消息给其他客户（通道）
     *
     * @param msg
     * @param self
     */
    private void sendMsgToOthers(String msg, SocketChannel self) throws IOException {
        System.out.println("服务器转发信息中，，，，，");
        //遍历所有注册到selector上的SocketChannel，并排除本通道
        for (SelectionKey key : selector.keys()) {
            //通过key取出对应的socketchannel
            //通过channel接口获取socketchannel，不用再强制转socketchannel
            Channel targetChannel = key.channel();
            //排除自己
            if (targetChannel instanceof SocketChannel && !targetChannel.equals(self)) {
                //转型
                //将msg存储到buffer
                ByteBuffer byteBuffer = ByteBuffer.wrap(msg.getBytes());
                //将buffer的数据写入通道
                ((SocketChannel) targetChannel).write(byteBuffer);

            }

        }

    }

    public static void main(String[] args) {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }
}
