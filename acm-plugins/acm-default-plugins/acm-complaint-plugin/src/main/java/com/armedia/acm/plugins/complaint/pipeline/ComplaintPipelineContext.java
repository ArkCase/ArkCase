package com.armedia.acm.plugins.complaint.pipeline;

import com.armedia.acm.services.pipeline.PipelineContext;
import org.springframework.security.core.Authentication;

/**
 * Store all the complaint saving-related references in this context.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 12.08.2015.
 */
public class ComplaintPipelineContext implements PipelineContext
{
    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }
}
