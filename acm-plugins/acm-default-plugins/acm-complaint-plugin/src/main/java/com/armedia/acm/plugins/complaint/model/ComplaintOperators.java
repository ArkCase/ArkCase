package com.armedia.acm.plugins.complaint.model;

import org.mule.api.annotations.param.InboundHeaders;
import org.mule.api.annotations.param.Payload;
import org.springframework.security.core.Authentication;

/**
 * Created by armdev on 4/9/14.
 */
public class ComplaintOperators
{

    public Complaint setCreatorModifier(
            @Payload Complaint in,
            @InboundHeaders("acmUser") Authentication user)
    {
        in.setModifier(user.getName());
        if ( in.getCreator() == null )
        {
            in.setCreator(user.getName());
        }

        return in;
    }
}
