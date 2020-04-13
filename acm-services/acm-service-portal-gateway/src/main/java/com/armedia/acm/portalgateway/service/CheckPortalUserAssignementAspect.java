package com.armedia.acm.portalgateway.service;

/*-
 * #%L
 * ACM Service: Portal Gateway Service
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
 * %%
 * This file is part of the ArkCase software.
 *
 * If the software was purchased under a paid ArkCase license, the terms of
 * the paid license agreement will prevail.  Otherwise, the software is
 * provided under the following open source license terms:
 *
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;

import java.lang.reflect.Parameter;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 15, 2018
 *
 */
@Aspect
@Component
public class CheckPortalUserAssignementAspect
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalCheckUserAssignementService checkUserAssignementService;

    @Around("@annotation(CheckPortalUserAssignement)")
    public Object isUserAssigned(ProceedingJoinPoint joinPoint) throws Throwable
    {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Parameter[] parameters = signature.getMethod().getParameters();
        Object[] methodArguments = joinPoint.getArgs();
        Authentication auth = null;
        String portalId = null;
        for (int i = 0; i < parameters.length; i++)
        {
            if (parameters[i].getType().equals(Authentication.class))
            {
                auth = (Authentication) methodArguments[i];
            }
            else if (isPortalId(parameters[i]))
            {
                portalId = (String) methodArguments[i];
            }
        }

        if (auth != null && portalId != null)
        {
            log.debug("Checking if user [{}] is assigned to portal with ID [{}].", auth.getName(), portalId);
            try
            {
                checkUserAssignementService.isUserAssigned(auth.getName(), portalId);
            }
            catch (NoResultException nre)
            {
                log.error("Portal not configured in ArkCase [{}]", portalId, nre);
                throw new PortalUserServiceException("Portal not configured in ArkCase!");
            }
        }
        else
        {
            log.warn(
                    "Probably not a proper way of using [{}] annotation. One argument has to be of [{}] type, and another [{}] anotated with [{}].",
                    CheckPortalUserAssignement.class.getName(), String.class.getName(), PortalId.class.getName());
            throw new PortalUserAssignementException(String.format(
                    "Probably not a proper way of using [%s] annotation. One argument has to be of [%s] type, and another [%s] anotated with [%s].",
                    CheckPortalUserAssignement.class.getName(), String.class.getName(), PortalId.class.getName()));
        }

        return joinPoint.proceed();
    }

    private boolean isPortalId(Parameter parameter)
    {
        try
        {
            return parameter.getAnnotationsByType(PortalId.class).length > 0;
        }
        catch (NullPointerException e)
        {
            return false;
        }
    }

    /**
     * @param checkUserAssignementService
     *            the checkUserAssignementService to set
     */
    public void setCheckUserAssignementService(PortalCheckUserAssignementService checkUserAssignementService)
    {
        this.checkUserAssignementService = checkUserAssignementService;
    }

    /**
     * @return the checkUserAssignementService
     */
    public PortalCheckUserAssignementService getCheckUserAssignementService()
    {
        return checkUserAssignementService;
    }

}
