package study.even.reactor.one_reactor_single_thread;

import java.nio.channels.*;

/**
 * @author: Even
 * @date: 2021-01-30
 */
public class Acceptor {
    private SelectionKey selectionKey;

    public Acceptor(SelectionKey key) {
        this.selectionKey = key;
    }

    /**
     * 处理accept
     *
     * @throws Exception
     */
    public void handle() throws Exception{
        Channel channel = selectionKey.channel();
        SocketChannel socketChannel = ((ServerSocketChannel) channel).accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selectionKey.selector(),SelectionKey.OP_ACCEPT);
    }
}
