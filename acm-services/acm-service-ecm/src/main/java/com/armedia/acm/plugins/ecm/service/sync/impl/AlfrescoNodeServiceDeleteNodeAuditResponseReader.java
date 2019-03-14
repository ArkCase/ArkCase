package com.armedia.acm.plugins.ecm.service.sync.impl;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.sync.EcmAuditResponseReader;

import org.json.JSONObject;

/**
 * Read Alfresco audit records generated from the Alfresco NodeService deleteNode method. Alfresco uses this method
 * to delete folders and files.
 */
public class AlfrescoNodeServiceDeleteNodeAuditResponseReader implements EcmAuditResponseReader
{

    @Override
    public EcmEvent buildEcmEvent(JSONObject deleteEvent)
    {
        // the deleteNode method won't generate very much audit information... we can only get the actual node ID
        // that was deleted.
        EcmEvent retval = new EcmEvent(deleteEvent);
        retval.setEcmEventType(EcmEventType.DELETE);

        Long auditId = deleteEvent.getLong("id");
        retval.setAuditId(auditId);

        retval.setUserId(deleteEvent.getString("user"));

        JSONObject values = deleteEvent.getJSONObject("values");

        String nodeId = values.getString("/auditarkcasedeleteextractors/delete/in/a");
        retval.setNodeId(nodeId);

        return retval;
    }
}
