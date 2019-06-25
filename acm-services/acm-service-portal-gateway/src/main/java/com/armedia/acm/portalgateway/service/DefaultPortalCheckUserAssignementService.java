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

import com.armedia.acm.portalgateway.model.PortalInfo;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 13, 2018
 *
 */
public class DefaultPortalCheckUserAssignementService implements PortalCheckUserAssignementService
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalInfoDAO portalInfoDao;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalCheckUserAssignementService#isUserAssigned(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void isUserAssigned(String userId, String portalId) throws PortalUserAssignementException
    {
        log.debug("Checking if user with ID [{}] is assigned to portal with [{}] ID.", userId, portalId);
        PortalInfo portalInfo = portalInfoDao.findByPortalId(portalId);
        if (!portalInfo.getUser().getUserId().equals(userId))
        {
            log.debug("User with ID [{}] is not assigned to portal with [{}] ID.", userId, portalId);
            throw new PortalUserAssignementException(
                    String.format("User with ID [%s] is not assigned to portal with [%s] ID.", userId, portalId));
        }
    }

    /**
     * @param portalInfoDao
     *            the portalInfoDao to set
     */
    public void setPortalInfoDao(PortalInfoDAO portalInfoDao)
    {
        this.portalInfoDao = portalInfoDao;
    }

}
