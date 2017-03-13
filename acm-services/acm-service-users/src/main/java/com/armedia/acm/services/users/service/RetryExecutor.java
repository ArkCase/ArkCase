package com.armedia.acm.services.users.service;

import com.armedia.acm.services.users.model.ldap.AcmLdapConstants;

import java.util.concurrent.Callable;

/**
 * Generic executor with multiple retries and given timeout between each retry
 */
public class RetryExecutor<T>
{
    private final long timeout;
    private int retryAttempts;

    public RetryExecutor()
    {
        this(AcmLdapConstants.RETRY_ATTEMPTS, AcmLdapConstants.RETRY_TIMEOUT);
    }

    public RetryExecutor(int retryAttempts, long timeout)
    {
        this.retryAttempts = retryAttempts;
        this.timeout = timeout;
    }

    public T retryResult(Callable<T> callable) throws Exception
    {
        try
        {
            retryAttempts--;
            return callable.call();
        } catch (Exception e)
        {
            if (retryAttempts > 0)
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
        } catch (Exception e)
        {
            if (retryAttempts > 0)
            {
                sleep(timeout);
                retry(action);
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
        } catch (Exception e)
        {
            if (retryAttempts > 0)
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
        } catch (InterruptedException e)
        {
        }
    }


}
