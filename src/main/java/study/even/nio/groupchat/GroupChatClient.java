package study.even.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class GroupChatClient {

    /*定义相关属性*/
    //服务器ip
    private final String SERVER_HOST = "127.0.0.1";
    //服务器端口
    private final int SERVER_PORT = 6667;
    //定义选择器
    private Selector selector;
    //定义通道
    private SocketChannel socketChannel;

    private String userName;

    public GroupChatClient() throws IOException {
        selector = Selector.open();
        //连接服务器
        socketChannel = SocketChannel.open(new InetSocketAddress(SERVER_HOST, SERVER_PORT));
        //设置非阻塞
        socketChannel.configureBlocking(false);
        //将channel注册到selector
        socketChannel.register(selector, SelectionKey.OP_READ);
        //得到用户名
        userName = socketChannel.getLocalAddress().toString().substring(1);
        System.out.println(userName + " is ready,,,,,,,");
    }

    /**
     * 向服务器发消息
     * @param info
     */
    public void sendInfo(String info) {
        info = userName + "说：" + info;
        try {
            socketChannel.write(ByteBuffer.wrap(info.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(info);
    }

    /**
     * 读取从服务器拿到的信息
     */
    public void readInfo() throws IOException {
        int readChannnels = selector. select();
        //有可用的通道
        if (readChannnels>0){
            Iterator<SelectionKey> selectionKeyIterator = selector.selectedKeys().iterator();
            while(selectionKeyIterator.hasNext()){
                SelectionKey selectionKey = selectionKeyIterator.next();
                if (selectionKey.isReadable()){
                    //得到相关通道
                    Channel channel = selectionKey.channel();
                    //得到一个buffer
                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    //读取
                    ((SocketChannel)channel).read(byteBuffer);
                    //把读取到的缓冲区数据转成字符串
                    String msg = new String(byteBuffer.array());
                    System.out.println(msg.trim());
                }

                //删除当前key，防止重复操作
                selectionKeyIterator.remove();
            }

        }

    }

    public static void main(String[] args) throws IOException {
        //启动客户端
        final GroupChatClient groupChatClient = new GroupChatClient();

        new Thread(new ClientThread(groupChatClient)).start();

        //发送数据给服务器端
        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            groupChatClient.sendInfo(line);
        }

    }

}
