package com.armedia.acm.web.api;

import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A {@link ThreadPoolExecutorFactoryBean} that returns a {@link MDCThreadPoolExecutor} instance.
 * <p>
 * Created by Bojan Milenkoski on 18.1.2016.
 */
public class MDCThreadPoolTaskExecutor extends ThreadPoolExecutorFactoryBean
{

    private static final long serialVersionUID = 1L;

    protected ThreadPoolExecutor createExecutor(int corePoolSize, int maxPoolSize, int keepAliveSeconds,
            BlockingQueue<Runnable> queue, ThreadFactory threadFactory,
            RejectedExecutionHandler rejectedExecutionHandler)
    {

        return new MDCThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveSeconds, TimeUnit.SECONDS, queue,
                threadFactory, rejectedExecutionHandler);
    }
}