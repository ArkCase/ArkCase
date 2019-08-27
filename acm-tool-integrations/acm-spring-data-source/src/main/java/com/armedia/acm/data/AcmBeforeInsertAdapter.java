package com.armedia.acm.data;

/*-
 * #%L
 * Tool Integrations: Spring Data Source
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


import com.armedia.acm.core.exceptions.AcmAccessControlException;

import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;

import javax.persistence.PersistenceException;

/**
 * Created by armdev on 11/24/14.
 */
class AcmBeforeInsertAdapter extends DescriptorEventAdapter
{
    private final AcmBeforeInsertListener beforeInsertListener;

    AcmBeforeInsertAdapter(AcmBeforeInsertListener beforeInsertListener)
    {
        this.beforeInsertListener = beforeInsertListener;
    }

    @Override
    public void preInsert(DescriptorEvent event)
    {
        super.preInsert(event);
        try
        {
            getBeforeInsertListener().beforeInsert(event.getObject());
        }
        catch (AcmAccessControlException e)
        {
            throw new PersistenceException(e);
        }
    }

    public AcmBeforeInsertListener getBeforeInsertListener()
    {
        return beforeInsertListener;
    }
}
