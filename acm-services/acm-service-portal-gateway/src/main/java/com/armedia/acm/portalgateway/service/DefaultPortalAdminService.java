/**
 *
 */
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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.service.ldap.LdapUserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 29, 2018
 *
 */

public class DefaultPortalAdminService implements PortalAdminService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private LdapUserService ldapUserService;

    private MessageChannel genericMessagesChannel;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalAdminService#generateId()
     */
    @Override
    public String generateId()
    {
        log.debug("Generating portal UUID.");
        return UUID.randomUUID().toString();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.portalgateway.service.PortalAdminService#getExceptionMapper(com.armedia.acm.portalgateway.service
     * .PortalServiceException)
     */
    @Override
    public PortalServiceExceptionMapper getExceptionMapper(PortalAdminServiceException se)
    {
        return new PortalAdminServiceExceptionMapper(se);
    }

    @Override
    @Async
    public void moveExistingLdapUsersToGroup(String newAcmGroup, PortalConfigurationService portalConfigurationService,
            String directoryName, Authentication auth)
    {
        try
        {
            ldapUserService.moveExistingLdapUsersToGroup(newAcmGroup, portalConfigurationService.getPortalConfiguration().getGroupName(),
                    directoryName);
            send(true, auth, portalConfigurationService);
        }
        catch (AcmLdapActionFailedException | AcmObjectNotFoundException e)
        {
            log.warn("Failed to move portal users to another configured group");
            send(false, auth, portalConfigurationService);
        }
    }

    private void send(Boolean action, Authentication auth, PortalConfigurationService portalConfigurationService)
    {
        log.debug("Send progress for moving portal users to another group");

        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("user", auth.getName());
        message.put("previousPortalInfo", portalConfigurationService.getPortalConfiguration());
        message.put("eventType", "portalUserProgress");
        Message<Map<String, Object>> progressMessage = MessageBuilder.withPayload(message).build();

        genericMessagesChannel.send(progressMessage);
    }

    public LdapUserService getLdapUserService()
    {
        return ldapUserService;
    }

    public void setLdapUserService(LdapUserService ldapUserService)
    {
        this.ldapUserService = ldapUserService;
    }

    public MessageChannel getGenericMessagesChannel()
    {
        return genericMessagesChannel;
    }

    public void setGenericMessagesChannel(MessageChannel genericMessagesChannel)
    {
        this.genericMessagesChannel = genericMessagesChannel;
    }
}
