package study.even.reactor.one_reactor_single_thread;

/**
 * @author: Even
 * @date: 2021-01-30
 */
public class OneReactorSingleThread {
    public static void main(String[] args) {
        int port = 6667;
        new Reactor(port);
    }
}
