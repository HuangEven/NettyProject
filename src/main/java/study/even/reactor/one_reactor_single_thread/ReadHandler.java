package study.even.reactor.one_reactor_single_thread;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author: Even
 * @date: 2021-01-30
 */
public class ReadHandler implements Handler {
    @Override
    public void handle(SelectionKey key) throws Exception{
        SocketChannel socketChannel = (SocketChannel) key.channel();
        //处理读
        doRead(socketChannel);
        //读完后，将通道注册为写
        socketChannel.register(key.selector(), SelectionKey.OP_WRITE);
    }

    /**
     * 处理读
     *
     * @param socketChannel
     */
    private void doRead(SocketChannel socketChannel){
        System.out.println("The Server is handing read........");
    }
}
