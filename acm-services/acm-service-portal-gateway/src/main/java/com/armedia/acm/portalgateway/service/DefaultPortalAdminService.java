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

import static com.armedia.acm.services.users.model.ldap.MapperUtils.prefixTrailingDot;

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.portalgateway.model.PortalInfo;
import com.armedia.acm.portalgateway.web.api.PortalInfoDTO;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.dao.group.AcmGroupDao;
import com.armedia.acm.services.users.model.AcmUser;
import com.armedia.acm.services.users.model.group.AcmGroup;
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;
import com.armedia.acm.services.users.model.ldap.AcmLdapSyncConfig;
import com.armedia.acm.services.users.service.ldap.LdapUserService;
import com.armedia.acm.services.users.web.api.SecureLdapController;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 29, 2018
 *
 */

public class DefaultPortalAdminService extends SecureLdapController implements PortalAdminService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalInfoDAO portalInfoDao;

    private UserDao userDao;

    private AcmGroupDao groupDao;

    @Value("${foia.portalserviceprovider.directory.name}")
    private String directoryName;

    private LdapUserService ldapUserService;

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
    public void addExistingPortalUsersToGroup(String groupName, String previousGroupName)
            throws AcmAppErrorJsonMsg, AcmLdapActionFailedException, AcmObjectNotFoundException
    {

        AcmLdapSyncConfig ldapSyncConfig = getAcmContextHolder().getAllBeansOfType(AcmLdapSyncConfig.class)
                .get(String.format("%s_sync", directoryName));

        String userPrefix = prefixTrailingDot(ldapSyncConfig.getUserPrefix());
        List<AcmUser> acmUsers = userDao.findByPrefix(userPrefix);

        List<String> userIds = acmUsers.stream().map(AcmUser::getUserId).collect(Collectors.toList());
        List<String> groupNames = new ArrayList<>(Arrays.asList(previousGroupName));

        checkIfLdapManagementIsAllowed(directoryName);

        for (String userId : userIds)
        {
            ldapUserService.removeUserFromGroups(userId, groupNames, directoryName);
        }

        ldapUserService.addExistingLdapUsersToGroup(userIds, directoryName, groupName);
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
}
