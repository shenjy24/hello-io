package com.jonas.threadmodel.singlethread;

/**
 * Reactor单线程模型Server
 * 参考资料：https://www.cnblogs.com/hama1993/p/10611229.html
 *
 * @author shenjy
 * @version 1.0
 * @date 2021-01-18
 */
public class Server {
    public static void main(String[] args) {
        new Thread(new ServerReactor(3000)).start();
    }
}
