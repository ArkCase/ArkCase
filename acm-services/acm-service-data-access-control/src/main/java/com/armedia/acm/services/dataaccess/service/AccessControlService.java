package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.services.dataaccess.model.AccessControlList;
import org.springframework.security.core.Authentication;

/**
 * Check if particular user is granted access to a given object.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 05.11.2015.
 */
public interface AccessControlService
{
    /**
     * Check if particular user is granted access to a given object.
     *
     * @param authentication authentication token
     * @param targetId       the identifier for the object instance
     * @param targetType     target type
     * @param permission     permission object
     * @return true if user is allowed to access this object, false otherwise
     */
    boolean isAccessGranted(Authentication authentication, Long targetId, String targetType, Object permission);


    /**
     * Getter method.
     *
     * @return configured ACL
     */
    AccessControlList getAccessControlList();
}
