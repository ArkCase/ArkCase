package com.armedia.acm.auth.okta.auth;

import com.armedia.acm.auth.AcmAuthenticationDetails;
import com.armedia.acm.auth.okta.model.user.OktaUser;

import javax.servlet.http.HttpServletRequest;

public class OktaAuthenticationDetails extends AcmAuthenticationDetails
{
    private OktaUser oktaUser;

    public OktaAuthenticationDetails(OktaUser user, HttpServletRequest request)
    {
        super(request);
        this.oktaUser = user;
    }

    public OktaAuthenticationDetails(HttpServletRequest request)
    {
        super(request);
    }

    public OktaUser getOktaUser()
    {
        return oktaUser;
    }

    public void setOktaUser(OktaUser oktaUser)
    {
        this.oktaUser = oktaUser;
    }
}
