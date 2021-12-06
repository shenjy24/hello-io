package com.jonas.serialization.messagepack;

import com.jonas.serialization.User;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * EchoClientHandler
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-27
 */
public class EchoClientHandler extends ChannelHandlerAdapter {
    private final int sendNumber;

    public EchoClientHandler(int sendNumber) {
        this.sendNumber = sendNumber;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        User[] users = listUser();
        for (User user : users) {
            ctx.write(user);
        }
        ctx.flush();
    }

    private User[] listUser() {
        User[] users = new User[sendNumber];
        for (int i = 0; i < sendNumber; i++) {
            User user = new User("ABCDEFG ---> " + i, i);
            users[i] = user;
        }
        return users;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("client receive the msgPack message : " + msg);
    }
}
