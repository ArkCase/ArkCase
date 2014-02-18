package com.armedia.acm.auth;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class AcmAuthenticationDetails extends WebAuthenticationDetails
{
    private String userIpAddress;

    /**
     * Records the remote address and will also set the session id if a session
     * already exists (it won't create one).
     *
     * @param request that the authentication request was received from
     */
    public AcmAuthenticationDetails(HttpServletRequest request) {
        super(request);
    }

    /**
     * Override this to return the user's actual IP address if behind a proxy server or load balancer.  Default
     * Spring Security behavior will show the proxy server / load balancer IP address, not the user's real IP
     * address.
     * @return the user's actual IP address, even when they are behind a proxy or load balancer
     */
    @Override
    public String getRemoteAddress() {
        return userIpAddress == null ? super.getRemoteAddress() : userIpAddress;
    }

    public String getUserIpAddress() {
        return userIpAddress;
    }

    public void setUserIpAddress(String userIpAddress) {
        this.userIpAddress = userIpAddress;
    }

}
