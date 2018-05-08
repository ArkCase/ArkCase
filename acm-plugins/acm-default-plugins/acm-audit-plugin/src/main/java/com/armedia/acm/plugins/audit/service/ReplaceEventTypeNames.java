package com.armedia.acm.plugins.audit.service;

/*-
 * #%L
 * ACM Default Plugin: Audit
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

import com.armedia.acm.audit.model.AuditEvent;
import com.armedia.acm.pluginmanager.model.AcmPlugin;
import com.armedia.acm.plugins.audit.model.AuditConstants;

public class ReplaceEventTypeNames
{

    private AcmPlugin pluginEventType;

    public AuditEvent replaceNameInAcmEvent(AuditEvent event)
    {
        String replacementName = (String) getPluginEventType().getPluginProperties()
                .get(AuditConstants.EVENT_TYPE + event.getFullEventType());
        if (replacementName == null)
        {
            event.setFullEventType(event.getFullEventType());
        }
        else
        {
            event.setFullEventType(replacementName);
        }
        return event;
    }

    public AcmPlugin getPluginEventType()
    {
        return pluginEventType;
    }

    public void setPluginEventType(AcmPlugin pluginEventType)
    {
        this.pluginEventType = pluginEventType;
    }
}
