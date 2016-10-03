package com.armedia.acm.auth;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utility class for common authentication related methods
 */
public class AuthenticationUtils
{
    /**
     * @return Client IP address
     */
    public static String getUserIpAddress()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() != null
                && authentication.getDetails() instanceof AcmAuthenticationDetails)
        {
            return ((AcmAuthenticationDetails) authentication.getDetails()).getRemoteAddress();
        }
        return null;
    }

    /**
     * @return Authenticated user principal name
     */
    public static String getUsername()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null)
        {
            return authentication.getName();
        }
        return null;
    }

}
