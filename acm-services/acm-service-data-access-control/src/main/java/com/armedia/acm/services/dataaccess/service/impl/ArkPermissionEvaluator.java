package com.armedia.acm.services.dataaccess.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

/**
 * Created by armdev on 2/12/15.
 */
public class ArkPermissionEvaluator implements PermissionEvaluator
{
    private final transient Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
    {
        if ( targetDomainObject == null )
        {
            log.error("Null targetDomainObject, refusing access!");
            return false;
        }

        log.info("Granting " + permission + " to " + authentication.getName() + " on object of type '" +
                targetDomainObject.getClass().getName() + "'");

        return true;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission)
    {
        if ( targetId == null )
        {
            log.error("Null targetId, refusing access!");
            return false;
        }

        log.info("Granting " + permission + " to " + authentication.getName() + " on object of type '" +
                targetType + "' with id '" + targetId + "'");

        return true;
    }
}
