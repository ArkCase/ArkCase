package com.armedia.acm.activiti.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by dmiller on 3/23/2017.
 */
public class ActivitiTaskScheduler extends ThreadPoolTaskExecutor implements ApplicationContextAware
{
    private AtomicBoolean initialized = new AtomicBoolean(false);

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public void afterPropertiesSet()
    {
        super.afterPropertiesSet();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task)
    {
        if ( !initialized.get() )
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }

        return super.submit(task);
    }

    @Override
    public Future<?> submit(Runnable task)
    {
        if ( !initialized.get() )
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }

        return super.submit(task);
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task)
    {
        if ( !initialized.get() )
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }
        return super.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task)
    {
        if ( !initialized.get() )
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }
        return super.submitListenable(task);
    }

    @Override
    public void execute(Runnable task)
    {
        if ( !initialized.get() )
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }
        super.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout)
    {
        if ( !initialized.get() )
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }
        super.execute(task, startTimeout);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        initialized.set(true);
        LOG.debug("Application has started - now accepting tasks.");
    }
}
