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
import com.armedia.acm.services.users.model.ldap.AcmLdapActionFailedException;

import org.springframework.security.core.Authentication;

import java.util.List;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 28, 2018
 *
 */
public interface PortalAdminService
{

    public static final String GET_INFO_METHOD = "GET_PORTAL_INFO_PORTAL_NOT_FOUND";

    public static final String UNREGISTER_METHOD = "UNREGISTER_PORTAL_PORTAL_NOT_FOUND";

    public static final String UPDATE_METHOD_USER = "UPDATE_PORTAL_USER_NOT_FOUND";

    public static final String UPDATE_METHOD_PORTAL = "UPDATE_PORTAL_PORTAL_NOT_FOUND";

    /**
     * @return
     */
    String generateId();

    /**
     * @return
     */
    List<PortalInfo> listRegisteredPortals();

    /**
     * @param portalId
     * @return
     * @throws PortalAdminServiceException
     */
    PortalInfo getPortalInfo(String portalId) throws PortalAdminServiceException;

    /**
     * @param portalInfo
     * @param groupName
     * @param string
     * @return
     */
    PortalInfo registerPortal(PortalInfo portalInfo, String userId, String groupName);

    /**
     * @param portalInfo
     * @param string
     * @return
     * @throws PortalAdminServiceException
     */
    PortalInfo updatePortal(PortalInfo portalInfo, String userId) throws PortalAdminServiceException;

    /**
     * @param portalId
     * @return
     * @throws PortalAdminServiceException
     */
    PortalInfo unregisterPortal(String portalId) throws PortalAdminServiceException;

    /**
     * @param se
     * @return
     */
    PortalServiceExceptionMapper getExceptionMapper(PortalAdminServiceException se);

    void updatePortalInfo(PortalInfo portalInfo, PortalInfoDTO portalInfoDTO);

    void moveExistingLdapUsersToGroup(String groupName, PortalInfo previousPortalInfo, String directoryName, Authentication auth)
            throws AcmLdapActionFailedException, AcmObjectNotFoundException;
}
