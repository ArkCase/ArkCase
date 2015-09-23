package com.armedia.acm.plugins.casefile.pipeline;

import com.armedia.acm.services.pipeline.PipelineContext;
import org.springframework.security.core.Authentication;

/**
 * Store all the case file saving-related references in this context.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 11.08.2015.
 */
public class CaseFilePipelineContext implements PipelineContext
{
    /**
     * Flag showing whether new case file is created.
     */
    private boolean newCase;

    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    /**
     * The queue that case file is already in.
     */
    private String queueName;

    /**
     * The queue the case will be moved to; used in the queue pipeline.
     */
    private String enqueueName;

    /**
     * IP Address.
     */
    private String ipAddress;

    public boolean isNewCase()
    {
        return newCase;
    }

    public void setNewCase(boolean newCase)
    {
        this.newCase = newCase;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public String getEnqueueName()
    {
        return enqueueName;
    }

    public void setEnqueueName(String enqueueName)
    {
        this.enqueueName = enqueueName;
    }

    public String getQueueName()
    {
        return queueName;
    }

    public void setQueueName(String queueName)
    {
        this.queueName = queueName;
    }
}
