package com.client.utils;

import com.client.service.UserService;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 获取线程池的工厂
 * 使用静态内部类实现单例模式
 * 保证只有一个线程池
 */
public class ThreadPoolFactory {
    private static final int corePoolSize;
    private static final int maximumPoolSize;
    private static final int workQueueSize;

    // 加载资源文件内容
    static {
        Properties properties = new Properties();
        try {
            properties.load(UserService.class.getResourceAsStream("threadPool.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        corePoolSize = Integer.parseInt(properties.get("corePoolSize").toString());
        maximumPoolSize = Integer.parseInt(properties.get("maximumPoolSize").toString());
        workQueueSize = Integer.parseInt(properties.get("workQueueSize").toString());
    }

    public static final ThreadPoolExecutor getThreadPool() {
        return SingletonHolder.pool;
    }

    /**
     * 静态内部类
     */
    private static class SingletonHolder {
        private static final ThreadPoolExecutor pool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize,
                2, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(workQueueSize), Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

}
