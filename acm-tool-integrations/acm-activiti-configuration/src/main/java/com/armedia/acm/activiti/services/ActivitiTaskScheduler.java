package com.armedia.acm.activiti.services;

/*-
 * #%L
 * Tool Integrations: Activiti Configuration
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
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
    private transient final Logger LOG = LogManager.getLogger(getClass());
    private AtomicBoolean initialized = new AtomicBoolean(false);

    @Override
    public void afterPropertiesSet()
    {
        super.afterPropertiesSet();
    }

    @Override
    public <T> Future<T> submit(Callable<T> task)
    {
        if (!initialized.get())
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }

        return super.submit(task);
    }

    @Override
    public Future<?> submit(Runnable task)
    {
        if (!initialized.get())
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }

        return super.submit(task);
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task)
    {
        if (!initialized.get())
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }
        return super.submitListenable(task);
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task)
    {
        if (!initialized.get())
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }
        return super.submitListenable(task);
    }

    @Override
    public void execute(Runnable task)
    {
        if (!initialized.get())
        {
            throw new TaskRejectedException("Application has not started yet; not starting task: " + task);
        }
        super.execute(task);
    }

    @Override
    public void execute(Runnable task, long startTimeout)
    {
        if (!initialized.get())
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
