package com.jonas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * BIO客户端
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-15
 */
public class TimeClient {

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                TimeClient client = new TimeClient();
                client.startConnect("127.0.0.1", 8080);
            }, "ClientThread" + i);
            thread.start();
        }
    }

    public void startConnect(String ip, int port) {
        Socket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("QUERY TIME ORDER");
            System.out.println("Send order to server succeed.");
            String receive = in.readLine();
            System.out.println("Now is : " + receive);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != out) {
                out.close();
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
