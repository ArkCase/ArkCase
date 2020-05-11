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
import com.armedia.acm.portalgateway.model.PortalInfo;
import com.armedia.acm.portalgateway.web.api.PortalInfoDTO;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.service.ldap.LdapUserService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 29, 2018
 *
 */

public class DefaultPortalAdminService implements PortalAdminService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalInfoDAO portalInfoDao;

    private UserDao userDao;

    private AcmGroupDao groupDao;

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
     * @see com.armedia.acm.portalgateway.service.PortalAdminService#listRegisteredPortals()
     */
    @Override
    public List<PortalInfo> listRegisteredPortals()
    {
        log.debug("Listing registered portals.");
        return portalInfoDao.findAll();
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalAdminService#getPortalInfo(java.lang.String)
     */
    @Override
    public PortalInfo getPortalInfo(String portalId) throws PortalAdminServiceException
    {
        log.debug("Retrieving portal info for portal with [{}] ID.", portalId);
        try
        {
            return portalInfoDao.findByPortalId(portalId);
        }
        catch (NoResultException e)
        {
            log.warn("Can't find portal info for portal ID [{}].", portalId);
            throw new PortalAdminServiceException(String.format("Can't find portal info for portal ID [%s].", portalId), e,
                    GET_INFO_METHOD);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalAdminService#registerPortal(com.armedia.acm.portalgateway.model.
     * PortalInfo, java.lang.String, java.lang.String)
     */
    @Override
    public PortalInfo registerPortal(PortalInfo portalInfo, String userId, String groupName)
    {
        AcmUser user = userDao.findByUserId(userId);
        AcmGroup group = groupDao.findByName(groupName);
        portalInfo.setPortalId(generateId());
        portalInfo.setUser(user);
        portalInfo.setGroup(group);
        log.debug("Registering portal for [{}] URL with portal ID [{}].", portalInfo.getPortalUrl(), portalInfo.getPortalId());
        return portalInfoDao.save(portalInfo);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalAdminService#updatePortal(com.armedia.acm.portalgateway.model.
     * PortalInfo, java.lang.String)
     */
    @Override
    public PortalInfo updatePortal(PortalInfo portalInfo, String userId) throws PortalAdminServiceException
    {
        log.debug("Updating portal for [{}] URL with portal ID [{}].", portalInfo.getPortalUrl(), portalInfo.getPortalId());
        try
        {
            AcmUser user = Optional.ofNullable(userDao.findByUserId(userId)).orElseThrow(() -> {
                PortalAdminServiceException ex = new PortalAdminServiceException(
                        String.format("Can't find user for user ID [%s].", userId), UPDATE_METHOD_USER);
                return ex;
            });
            PortalInfo existing = portalInfoDao.findByPortalId(portalInfo.getPortalId());

            existing.setPortalDescription(portalInfo.getPortalDescription());
            existing.setPortalUrl(portalInfo.getPortalUrl());
            existing.setUser(user);
            existing.setGroup(portalInfo.getGroup());
            existing.setPortalAuthenticationFlag(portalInfo.getPortalAuthenticationFlag());

            return portalInfoDao.save(existing);
        }
        catch (NoResultException e)
        {
            log.warn("Can't find portal info for portal ID [{}].", portalInfo.getPortalId());
            throw new PortalAdminServiceException(String.format("Can't find portal info for portal ID [%s].", portalInfo.getPortalId()), e,
                    UPDATE_METHOD_PORTAL);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalAdminService#unregisterPortal(java.lang.String)
     */
    @Override
    @Transactional(rollbackFor = PortalAdminServiceException.class)
    public PortalInfo unregisterPortal(String portalId) throws PortalAdminServiceException
    {
        log.debug("Unregistering portal with portal ID [{}].", portalId);
        try
        {
            PortalInfo portalInfo = portalInfoDao.findByPortalId(portalId);
            portalInfoDao.getEm().remove(portalInfo);
            return portalInfo;
        }
        catch (NoResultException e)
        {
            log.warn("Can't find portal info for portal ID [{}].", portalId);
            throw new PortalAdminServiceException(String.format("Can't find portal info for portal ID [%s].", portalId), e,
                    UNREGISTER_METHOD);
        }

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
    public void updatePortalInfo(PortalInfo portalInfo, PortalInfoDTO portalInfoDTO)
    {
        AcmGroup group = groupDao.findByName(portalInfoDTO.getGroupName());
        portalInfo.setGroup(group);
        portalInfo.setPortalDescription(portalInfoDTO.getPortalDescription());
        portalInfo.setPortalUrl(portalInfoDTO.getPortalUrl());
        portalInfo.setPortalAuthenticationFlag(portalInfoDTO.getPortalAuthenticationFlag());
    }

    @Override
    @Async
    public void moveExistingLdapUsersToGroup(String newAcmGroup, PortalInfo previousPortalInfo, Authentication auth)
    {
        try
        {
            ldapUserService.moveExistingLdapUsersToGroup(newAcmGroup, previousPortalInfo.getGroup().getName());
            send(true, auth, previousPortalInfo);
        }
        catch (AcmLdapActionFailedException | AcmObjectNotFoundException e)
        {
            log.warn("Failed to move portal users to another configured group");
            send(false, auth, previousPortalInfo);
        }
    }

    private void send(Boolean action, Authentication auth, PortalInfo portalInfo)
    {
        log.debug("Send progress for moving portal users to another group");

        Map<String, Object> message = new HashMap<>();
        message.put("action", action);
        message.put("user", auth.getName());
        message.put("previousPortalInfo", portalInfo);
        message.put("eventType", "portalUserProgress");
        Message<Map<String, Object>> progressMessage = MessageBuilder.withPayload(message).build();

        genericMessagesChannel.send(progressMessage);
    }

    /**
     * @param portalInfoDao
     *            the portalInfoDao to set
     */
    public void setPortalInfoDao(PortalInfoDAO portalInfoDao)
    {
        this.portalInfoDao = portalInfoDao;
    }

    /**
     * @param userDao
     *            the userDao to set
     */
    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    /**
     * @param groupDao
     *            the groupDao to set
     */
    public void setGroupDao(AcmGroupDao groupDao)
    {
        this.groupDao = groupDao;
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
