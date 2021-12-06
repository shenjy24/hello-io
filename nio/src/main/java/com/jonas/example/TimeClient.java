package com.jonas.example;

/**
 * TimeClient
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-15
 */
public class TimeClient {

    public static void main(String[] args) {
        TimeClient timeClient = new TimeClient();
        timeClient.startConnect("127.0.0.1", 8080);
    }

    public void startConnect(String host, int post) {
        new Thread(new TimeClientHandler(host, post), "TimeClient-001").start();
    }
}
