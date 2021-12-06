package com.jonas.threadmodel.client;

/**
 * Client
 *
 * @author shenjy
 * @version 1.0
 * @date 2021-01-18
 */
public class Client {
    public static void main(String[] args) {
        new Thread(new ClientReactor("127.0.0.1", 3000)).start();
        new Thread(new ClientReactor("127.0.0.1", 3000)).start();
    }
}
