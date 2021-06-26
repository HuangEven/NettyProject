package study.even.netty.dubbo.public_interface;

/**
 * 服务方和提供方都需要
 */
public interface HelloService {

    /**
     * 调用方法
     * @param str
     * @return
     */
    public String hello(String str);
}
