package com.armedia.acm.plugins.complaint.pipeline;

import com.armedia.acm.plugins.complaint.model.CloseComplaintRequest;
import com.armedia.acm.plugins.complaint.model.Complaint;
import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.springframework.security.core.Authentication;

public class CloseComplaintPipelineContext extends AbstractPipelineContext
{
    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    /*
    * 
    * */
    private boolean allowCloseComplaint;

    /**
     * IP Address.
     */
    private String ipAddress;

    /**
     * Complaint.
     */
    private Complaint complaint;

    /**
     * Close Complaint Request.
     */
    private CloseComplaintRequest closeComplaintRequest;

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }

    public boolean isAllowCloseComplaint()
    {
        return allowCloseComplaint;
    }

    public void setAllowCloseComplaint(boolean allowCloseComplaint)
    {
        this.allowCloseComplaint = allowCloseComplaint;
    }

    public String getIpAddress()
    {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress)
    {
        this.ipAddress = ipAddress;
    }

    public Complaint getComplaint()
    {
        return complaint;
    }

    public void setComplaint(Complaint complaint)
    {
        this.complaint = complaint;
    }

    public CloseComplaintRequest getCloseComplaintRequest()
    {
        return closeComplaintRequest;
    }

    public void setCloseComplaintRequest(CloseComplaintRequest closeComplaintRequest)
    {
        this.closeComplaintRequest = closeComplaintRequest;
    }
}
