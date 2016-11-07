package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public abstract class AlfrescoService<T>
{
    private String protocol;
    private String host;
    private String port;
    private String contextRoot;

    private short maxAttempts = 10;
    private short backoffMillis = 500;

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public String baseUrl()
    {
        String url = getProtocol() + "://" + getHost() + ":" + getPort() + "/" + getContextRoot();
        return url;
    }

    public final T service(Map<String, Object> context) throws AlfrescoServiceException
    {
        short attempt = 0;
        AlfrescoServiceException lastException = null;

        while (attempt < maxAttempts)
        {
            try
            {
                T retval = doService(context);
                return retval;
            } catch (AlfrescoServiceException e)
            {
                LOG.warn("Exception in service attempt # {}: {} {}", attempt, e.getMessage(), e);
                attempt++;
                lastException = e;
                try
                {
                    Thread.sleep(backoffMillis);
                } catch (InterruptedException e1)
                {
                    LOG.warn("Could not wait for the backoff period: {} {}", e1.getMessage(), e1);
                }
            }
        }

        LOG.error("Max attempts of {} have failed, throwing exception {} {}", maxAttempts, lastException.getMessage(), lastException);
        throw lastException;

    }

    public abstract T doService(Map<String, Object> context) throws AlfrescoServiceException;

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String protocol)
    {
        this.protocol = protocol;
    }

    public String getHost()
    {
        return host;
    }

    public void setHost(String host)
    {
        this.host = host;
    }

    public String getPort()
    {
        return port;
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public String getContextRoot()
    {
        return contextRoot;
    }

    public void setContextRoot(String contextRoot)
    {
        this.contextRoot = contextRoot;
    }
}
