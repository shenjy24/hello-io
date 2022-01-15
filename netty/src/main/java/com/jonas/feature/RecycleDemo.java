package com.jonas.feature;

import io.netty.util.Recycler;

/**
 * RecycleDemo
 *
 * @author shenjy
 * @version 1.0
 * @date 2022-01-15
 */
public class RecycleDemo {

    private static final Recycler<User> RECYCLER = new Recycler<User>() {
        @Override
        protected User newObject(Handle<User> handle) {
            return new User(handle);
        }
    };

    public static void main(String[] args) {
        User u1 = RECYCLER.get();
        u1.recycle();
        User u2 = RECYCLER.get();
        u2.recycle();
        System.out.println(u1 == u2);
    }

    static class User {
        private final Recycler.Handle<User> handle;

        public User(Recycler.Handle<User> handle) {
            this.handle = handle;
        }

        public void recycle() {
            handle.recycle(this);
        }
    }
}
