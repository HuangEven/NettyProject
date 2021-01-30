package study.even.reactor.one_reactor_single_thread;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * @author: Even
 * @date: 2021-01-30
 */
public class Reactor {
    //选择器
    private Selector selector;

    //监听通道
    private ServerSocketChannel serverSocketChannel;

    //设置内核监听队列长度
    private final static int LISTEN_QUEUE_LEN = 1024;

    public Reactor(int port) {
        try {
            //得到选择器
            selector = Selector.open();
            //打开通道
            serverSocketChannel = ServerSocketChannel.open();
            //绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(port), LISTEN_QUEUE_LEN);
            //设置非阻塞模式
            serverSocketChannel.configureBlocking(false);
            //将通道注册到selector
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            System.out.println("The Server is started in port:" + port + "........");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Reactor开始工作
     *
     */
    public void listen() {
        //循环监听
        while (true) {
            try {
                //监听有哪些事件发生，如不设置，将阻塞
                int count = selector.select(2000);

                //有事件待处理
                if (count > 0) {
                    //遍历得到的selectionKey集合
                    Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
                    while (keyIterator.hasNext()) {
                        //取出selectionKey
                        SelectionKey selectionKey = keyIterator.next();
                        //分发
                        try {

                        } catch (Exception e) {
                            //取消注册
                            selectionKey.cancel();
                            //关闭通道
                            serverSocketChannel.close();
                        }

                        //当前Key删除，防止重复处理
                        keyIterator.remove();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 分发
     *
     * @param key
     * @throws Exception
     */
    private void dispatcher(SelectionKey key) throws Exception {
        //验证selectionKey有效后开始处理，交由Acceptor进行任务派发
        if (key.isValid()) {
            if (key.isAcceptable()) {
                new Acceptor(key).handle();
            }
            if (key.isReadable()) {
                new ReadHandler().handle(key);
            }
            if (key.isWritable()) {
                new WriteHandler().handle(key);
            }
        }
    }

}
