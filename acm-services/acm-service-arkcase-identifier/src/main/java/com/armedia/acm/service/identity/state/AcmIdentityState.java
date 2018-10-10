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

import com.armedia.acm.service.stateofarkcase.interfaces.StateOfModule;

public class AcmIdentityState extends StateOfModule
{
    /**
     * domain for
     */
    private String domain;
    /**
     * local instance id
     */
    private String instanceID;
    /**
     * global shared id
     */
    private String globalID;

    public String getInstanceID()
    {
        return instanceID;
    }

    public void setInstanceID(String instanceID)
    {
        this.instanceID = instanceID;
    }

    public String getGlobalID()
    {
        return globalID;
    }

    public void setGlobalID(String globalID)
    {
        this.globalID = globalID;
    }

    public String getDomain()
    {
        return domain;
    }

    public void setDomain(String domain)
    {
        this.domain = domain;
    }
}
