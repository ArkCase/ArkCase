package com.armedia.acm.webdav;

import org.springframework.security.core.Authentication;

import io.milton.http.SecurityManager;

/**
 *
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity
 */
public interface AcmWebDAVSecurityManager extends SecurityManager
{

    Authentication getAuthentication();

    void setAuthentication(Authentication authentication);

}