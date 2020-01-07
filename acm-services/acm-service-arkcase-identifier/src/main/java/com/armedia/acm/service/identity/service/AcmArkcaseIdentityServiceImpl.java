package com.armedia.acm.service.identity.service;

/*-
 * #%L
 * ACM Service: Arkcase Identity
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

import com.armedia.acm.service.identity.dao.AcmArkcaseGlobalIdentityDao;
import com.armedia.acm.service.identity.dao.AcmArkcaseLocalIdentityDao;
import com.armedia.acm.service.identity.exceptions.AcmIdentityException;
import com.armedia.acm.service.identity.exceptions.AcmIdentityNotReadyException;
import com.armedia.acm.service.identity.model.AcmArkcaseIdentity;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Provides creating and retrieving arkcase identity
 */
public class AcmArkcaseIdentityServiceImpl implements AcmArkcaseIdentityService, ApplicationListener<ContextRefreshedEvent>
{
    private transient final Logger log = LogManager.getLogger(getClass());
    private boolean initialized = false;
    private AcmArkcaseIdentity identity;
    private AcmArkcaseLocalIdentityDao localIdentityDao;
    private AcmArkcaseGlobalIdentityDao globalIdentityDao;

    @Override
    public AcmArkcaseIdentity getIdentity() throws AcmIdentityException
    {
        if (!initialized)
        {
            throw new AcmIdentityNotReadyException("Not initialized yet.");
        }

        return identity;
    }

    /**
     * Handle an application event.
     *
     * @param event
     *            the event to respond to
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        if (((ApplicationContext) event.getSource()).getParent() == null)
        {
            identity = new AcmArkcaseIdentity();
            try
            {
                identity.setInstanceID(localIdentityDao.createIdentityIfNotExists());
            }
            catch (AcmIdentityException e)
            {
                log.error("Couldn't set local identity. [{}]", e.getMessage());
            }
            try
            {
                identity.setGlobalID(globalIdentityDao.getIdentity());
            }
            catch (AcmIdentityException e)
            {
                log.error("Couldn't set global identity. [{}]", e.getMessage());
            }
            initialized = true;
        }
    }

    public void setLocalIdentityDao(AcmArkcaseLocalIdentityDao localIdentityDao)
    {
        this.localIdentityDao = localIdentityDao;
    }

    public void setGlobalIdentityDao(AcmArkcaseGlobalIdentityDao globalIdentityDao)
    {
        this.globalIdentityDao = globalIdentityDao;
    }
}
