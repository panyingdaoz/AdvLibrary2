package com.kingbird.library.manager;

import android.support.annotation.NonNull;

import com.kingbird.library.utils.Plog;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程管理类
 *
 * @author panyingdao
 * @date 2017-8-22.
 */
public class ThreadManager {

    private static ThreadManager threadManager;
    private ExecutorService executorService;

    private ThreadManager() {
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        Plog.e("线程数", numberOfCores);
        TimeUnit keepAliveTimeUnit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();

        ThreadFactory namedThredFactory = new BasicThreadFactory.Builder().namingPattern("threadmanager-pool-%d").build();
        executorService = new ThreadPoolExecutor(numberOfCores, numberOfCores * 8,
                0L, keepAliveTimeUnit, taskQueue, namedThredFactory, new ThreadPoolExecutor.AbortPolicy());
    }

    public static ThreadManager getInstance() {
        if (threadManager == null) {
            synchronized (ThreadManager.class) {
                if (threadManager == null) {
                    threadManager = new ThreadManager();
                }
            }
        }
        return threadManager;
    }

    public void doExecute(@NonNull Runnable runnable) {
        executorService.execute(runnable);
    }

    public <T> Future<T> submit(@NonNull Callable<T> task) {
        return executorService.submit(task);
    }

    public void shutdown() {
        if (executorService.isShutdown()){
            executorService.shutdown();
        }
    }
}
