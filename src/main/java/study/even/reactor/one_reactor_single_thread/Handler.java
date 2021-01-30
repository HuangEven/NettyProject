package study.even.reactor.one_reactor_single_thread;

import java.nio.channels.SelectionKey;

/**
 * @author: Even
 * @date: 2021-01-30
 */
public interface Handler {
    /**
     * 业务处理
     *
     * @param key
     */
    public void handle(SelectionKey key) throws Exception;
}
