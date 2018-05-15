package com.armedia.acm.service.identity.state;

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

import com.armedia.acm.service.identity.exceptions.AcmIdentityException;
import com.armedia.acm.service.identity.model.AcmArkcaseIdentity;
import com.armedia.acm.service.identity.service.AcmArkcaseIdentityService;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;
import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModuleProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class AcmIdentityStateProvider implements StateOfModuleProvider
{
    private transient final Logger log = LoggerFactory.getLogger(getClass());
    private AcmArkcaseIdentityService acmArkcaseIdentityService;

    @Override
    public String getModuleName()
    {
        return "acm-identity";
    }

    @Override
    public StateOfModule getModuleState()
    {
        return getModuleState(LocalDate.now());
    }

    @Override
    public StateOfModule getModuleState(LocalDate day)
    {
        AcmIdentityState acmIdentityState = new AcmIdentityState();
        try
        {
            AcmArkcaseIdentity identity = acmArkcaseIdentityService.getIdentity();
            acmIdentityState.setGlobalID(identity.getGlobalID());
            acmIdentityState.setInstanceID(identity.getInstanceID());
        }
        catch (AcmIdentityException e)
        {
            log.error("Not able to provide identity state.", e.getMessage());
        }
        return acmIdentityState;
    }

    public void setAcmArkcaseIdentityService(AcmArkcaseIdentityService acmArkcaseIdentityService)
    {
        this.acmArkcaseIdentityService = acmArkcaseIdentityService;
    }
}
