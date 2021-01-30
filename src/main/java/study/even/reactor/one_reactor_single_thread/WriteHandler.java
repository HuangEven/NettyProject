package study.even.reactor.one_reactor_single_thread;

import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author: Even
 * @date: 2021-01-30
 */
public class WriteHandler implements Handler {
    @Override
    public void handle(SelectionKey key) throws Exception {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        //处理写
        doWrite(socketChannel);
        //写完后，将通道注册为读
        socketChannel.register(key.selector(), SelectionKey.OP_READ);
    }

    /**
     * 处理写
     *
     * @param socketChannel
     */
    private void doWrite(SocketChannel socketChannel) {
        System.out.println("The Server id handing Write......");
    }
}
