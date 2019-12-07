package com.kingbird.library.manager;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 定时管理类
 *
 * @author panyingdao
 * @date 2018/6/12/012.
 */
public class ExecutorServiceManager {

    private static ExecutorServiceManager executorServiceManager;
    private ScheduledExecutorService scheduledExecutorService;

    private ExecutorServiceManager() {
        int numberOfCores = Runtime.getRuntime().availableProcessors();
        scheduledExecutorService = new ScheduledThreadPoolExecutor(numberOfCores,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
    }

    public static ExecutorServiceManager getInstance() {
        if (executorServiceManager == null) {
            synchronized (ThreadManager.class) {
                if (executorServiceManager == null) {
                    executorServiceManager = new ExecutorServiceManager();
                }
            }
        }
        return executorServiceManager;
    }

    public void schedule(Runnable runnable,
                         long delay, TimeUnit unit) {
        if (!scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.schedule(runnable, delay, unit);
        }
    }

    public void scheduleAtFixedRate(Runnable command,
                                    long initialDelay,
                                    long period,
                                    TimeUnit unit) {
        try {
            if (!scheduledExecutorService.isShutdown()) {
                scheduledExecutorService.scheduleAtFixedRate(command, initialDelay, period, unit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        scheduledExecutorService.shutdown();
    }
}
