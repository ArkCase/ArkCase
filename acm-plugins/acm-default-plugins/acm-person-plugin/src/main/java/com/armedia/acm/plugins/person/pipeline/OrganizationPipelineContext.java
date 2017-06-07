package com.armedia.acm.plugins.person.pipeline;

import com.armedia.acm.services.pipeline.AbstractPipelineContext;
import org.springframework.security.core.Authentication;

/**
 * Store all the organization saving-related references in this context.
 */
public class OrganizationPipelineContext extends AbstractPipelineContext
{
    /**
     * Flag showing whether new organization is created.
     */
    private boolean newOrganization;

    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    /**
     * IP Address.
     */
    private String ipAddress;

    public boolean isNewOrganization()
    {
        return newOrganization;
    }

    public void setNewOrganization(boolean newOrganization)
    {
        this.newOrganization = newOrganization;
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
}
