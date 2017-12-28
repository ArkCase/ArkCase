package com.armedia.acm.services.users.service;

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
    private int retryAttempts;
    private final List<Class> exceptions;

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
