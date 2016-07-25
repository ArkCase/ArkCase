package com.armedia.acm.webdav;

import org.springframework.security.core.Authentication;

import io.milton.http.SecurityManager;

/**
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public interface AcmWebDAVSecurityManager extends SecurityManager
{

    Authentication getAuthenticationForTicket(String acmTicket);

    Authentication addAuthenticationForTicket(String acmTicket);

    void removeAuthenticationForTicket(String acmTicket);

}