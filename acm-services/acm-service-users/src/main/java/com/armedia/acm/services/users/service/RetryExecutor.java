package com.armedia.acm.services.users.service;

/*-
 * #%L
 * ACM Service: Users
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

import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;

import org.springframework.ldap.ServiceUnavailableException;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Generic executor with multiple retries and given timeout between each retry
 */
public class RetryExecutor<T>
{
    private final long timeout;
    private final List<Class> exceptions;
    private int retryAttempts;

    public RetryExecutor()
    {
        this(AcmLdapConstants.RETRY_ATTEMPTS, AcmLdapConstants.RETRY_TIMEOUT,
                Collections.singletonList(ServiceUnavailableException.class));
    }

    public RetryExecutor(int retryAttempts, long timeout, List<Class> exceptions)
    {
        this.retryAttempts = retryAttempts;
        this.timeout = timeout;
        this.exceptions = exceptions;
    }

    public T retryResult(Callable<T> callable) throws Exception
    {
        try
        {
            retryAttempts--;
            return callable.call();
        }
        catch (Exception e)
        {
            if (retryOnException(e) && retryAttempts > 0)
            {
                sleep(timeout);
                return retryResult(callable);
            }
            throw e;
        }
    }

    public void retry(Runnable action) throws Exception
    {
        try
        {
            retryAttempts--;
            action.run();
        }
        catch (Exception e)
        {
            if (retryOnException(e) && retryAttempts > 0)
            {
                sleep(timeout);
                retry(action);
            }
            throw e;
        }
    }

    public void retryChecked(CheckedRunnable action) throws Exception
    {
        try
        {
            retryAttempts--;
            action.execute();
        }
        catch (Exception e)
        {
            if (retryOnException(e) && retryAttempts > 0)
            {
                sleep(timeout);
                retryChecked(action);
            }
            throw e;
        }
    }

    public void retrySilent(Runnable action)
    {
        try
        {
            retryAttempts--;
            action.run();
        }
        catch (Exception e)
        {
            if (retryOnException(e) && retryAttempts > 0)
            {
                sleep(timeout);
                retrySilent(action);
            }

        }
    }

    private void sleep(final long timeout)
    {
        try
        {
            Thread.sleep(timeout);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    private boolean retryOnException(Exception e)
    {
        return exceptions.isEmpty() ||
                exceptions.stream().anyMatch(it -> e.getClass().isAssignableFrom(it));

    }

    @FunctionalInterface
    public interface CheckedRunnable
    {
        void execute() throws Exception;
    }

}
