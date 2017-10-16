package com.armedia.acm.plugins.person.pipeline;

import com.armedia.acm.services.pipeline.AbstractPipelineContext;

import org.springframework.security.core.Authentication;

/**
 * Created by bojan.milenkoski on 08.8.2017
 */
public class PersonPipelineContext extends AbstractPipelineContext
{
    /**
     * Flag showing whether new person is created.
     */
    private boolean newPerson;

    /**
     * Spring authentication token.
     */
    private Authentication authentication;

    public boolean isNewPerson()
    {
        return newPerson;
    }

    public void setNewPerson(boolean newPerson)
    {
        this.newPerson = newPerson;
    }

    public Authentication getAuthentication()
    {
        return authentication;
    }

    public void setAuthentication(Authentication authentication)
    {
        this.authentication = authentication;
    }
}
