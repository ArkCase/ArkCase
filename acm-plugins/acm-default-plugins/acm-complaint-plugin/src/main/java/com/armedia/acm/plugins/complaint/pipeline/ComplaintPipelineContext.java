package com.armedia.acm.plugins.complaint.pipeline;

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.springframework.security.core.Authentication;

/**
 * Store all the complaint saving-related references in this context.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 12.08.2015.
 */
public class ComplaintPipelineContext extends AbstractPipelineContext
{
    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    /**
     * Flag showing whether new complaint is created.
     */
    private boolean newComplaint;

    /**
     * IP Address.
     */
    private String ipAddress;

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public boolean isNewComplaint()
    {
        return newComplaint;
    }

    public void setNewComplaint(boolean newComplaint)
    {
        this.newComplaint = newComplaint;
    }
}
