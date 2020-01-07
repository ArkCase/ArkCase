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

import static com.armedia.acm.portalgateway.model.PortalInfo.FIND_PORTAL_INFO_BY_ID;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.portalgateway.model.PortalInfo;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity May 29, 2018
 *
 */
@Transactional
public class PortalInfoDAO extends AcmAbstractDao<PortalInfo>
{
    private transient final Logger log = LogManager.getLogger(getClass());

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.data.AcmAbstractDao#getPersistenceClass()
     */
    @Override
    protected Class<PortalInfo> getPersistenceClass()
    {
        return PortalInfo.class;
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.data.AcmAbstractDao#save(java.lang.Object)
     */
    @Override
    @CachePut(value = "portal_info_cache", key = "#portalInfo.portalId")
    public PortalInfo save(PortalInfo portalInfo)
    {
        log.debug("Saving portal info for portal with [{}] ID registered for [{}] URL.", portalInfo.getPortalId(),
                portalInfo.getPortalUrl());
        // wrapped call to be able to add the caching annotations.
        return super.save(portalInfo);
    }

    /**
     * @param portalId
     * @return
     */
    @Cacheable(value = "portal_info_cache")
    public PortalInfo findByPortalId(String portalId)
    {
        log.debug("Retrieving portal info for portal registered with [{}] ID.", portalId);
        return getEm().createNamedQuery(FIND_PORTAL_INFO_BY_ID, PortalInfo.class).setParameter("portalId", portalId).getSingleResult();
    }

}
