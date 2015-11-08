package com.armedia.acm.services.dataaccess.service;

import com.armedia.acm.services.dataaccess.model.AccessControlRules;
import org.springframework.security.core.Authentication;

/**
 * Check if particular user is granted access to a given object.
 * Created by Petar Ilin <petar.ilin@armedia.com> on 05.11.2015.
 */
public interface AccessControlRuleChecker
{
    /**
     * Check if particular user is granted access to a given object.
     *
     * @param authentication authentication token
     * @param targetId       the identifier for the object instance
     * @param targetType     target type
     * @param permission     required permission
     * @return true if user is allowed to access this object, false otherwise
     */
    boolean isAccessGranted(Authentication authentication, Long targetId, String targetType, String permission);


    /**
     * Getter method.
     *
     * @return configured AC rules
     */
    AccessControlRules getAccessControlRules();

    /**
     * Setter method.
     *
     * @param accessControlRules AC rules
     */
    void setAccessControlRules(AccessControlRules accessControlRules);
}
