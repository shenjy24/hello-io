package com.jonas;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TimeServerHandlerExecutorPool
 *
 * @author shenjy
 * @version 1.0
 * @date 2020-03-15
 */
public class TimeServerHandlerExecutorPool {
    private ExecutorService executor;

    public TimeServerHandlerExecutorPool(int maxPoolSize, int queueSize) {
        executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize, 120L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueSize));
    }

    public void execute(Runnable task) {
        executor.execute(task);
    }
}
