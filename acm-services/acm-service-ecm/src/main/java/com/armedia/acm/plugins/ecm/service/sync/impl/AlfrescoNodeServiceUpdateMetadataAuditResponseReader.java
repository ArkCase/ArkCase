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

import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.sync.EcmAuditResponseReader;
import com.google.common.collect.ImmutableMap;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Read Alfresco audit records generated from the Alfresco NodeService addProperties method. Alfresco uses this
 * method to add properties to the node.
 *
 * @author ivana.shekerova on 1/18/2019.
 */
public class AlfrescoNodeServiceUpdateMetadataAuditResponseReader implements EcmAuditResponseReader
{
    private final Map<String, String> alfrescoTypeToArkCaseType = ImmutableMap
            .of("{http://www.alfresco.org/model/content/1.0}content", EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT,
                    "{http://www.alfresco.org/model/content/1.0}folder", EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);

    @Override
    public EcmEvent buildEcmEvent(JSONObject updateEvent)
    {
        JSONObject values = updateEvent.getJSONObject("values");

        String alfrescoContentType = values.getString("/auditarkcasemetadata/update/derived/node-type");

        if (alfrescoTypeToArkCaseType.containsKey(alfrescoContentType))
        {

            EcmEvent retval = new EcmEvent(updateEvent);
            retval.setEcmEventType(EcmEventType.UPDATE);
            retval.setUserId(updateEvent.getString("user"));

            long auditId = updateEvent.getLong("id");
            retval.setAuditId(auditId);

            retval.setNodeType(alfrescoTypeToArkCaseType.get(alfrescoContentType));

            String nodeId = values.getString("/auditarkcasemetadata/update/in/a");
            retval.setNodeId(nodeId);

            String nodeName = values.getString("/auditarkcasemetadata/update/derived/node-name");
            retval.setNodeName(nodeName);

            String properties = values.getString("/auditarkcasemetadata/update/in/b");
            retval.setProperties(mapProperties(properties));

            return retval;
        }

        return null;

    }

    public Map<String, String> mapProperties(String properties)
    {
        HashMap<String, String> alfrescoPropertiesMap = new HashMap<>();
        properties = properties.trim().substring(1, properties.length() - 1);
        List<String> propertiesList = Arrays.asList(properties.split(","));

        for (String item : propertiesList)
        {
            String[] parts = item.trim().split("=");
            if (parts.length == 1 || (parts.length>1 && parts[1].equals("null")))
            {
                alfrescoPropertiesMap.put(parts[0], null);
            }
            else
            {
                alfrescoPropertiesMap.put(parts[0], parts[1]);
            }
        }

        return alfrescoPropertiesMap;
    }
}
