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

import java.util.Map;

/**
 * Read Alfresco audit records generated from the Alfresco NodeService createNode method. Alfresco uses the NodeService
 * to create folders, and also to create thumbnails and some other objects (but NOT to create content files... Alfresco
 * uses the FileFolderService to create content files).
 * <p>
 * For now, from the node service we are interested only in new folders. This reader ignores all other new
 * content types.
 */
public class AlfrescoNodeServiceCreateNodeAuditResponseReader implements EcmAuditResponseReader
{
    private final Map<String, String> alfrescoTypeToArkCaseType = ImmutableMap
            .of("{http://www.alfresco.org/model/content/1.0}folder", EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);

    @Override
    public EcmEvent buildEcmEvent(JSONObject createEvent)
    {
        // this reader only cares about folders
        JSONObject values = createEvent.getJSONObject("values");

        String alfrescoContentType = values.getString("/auditarkcaseextractors/create/in/b");

        if (alfrescoTypeToArkCaseType.containsKey(alfrescoContentType))
        {

            EcmEvent retval = new EcmEvent(createEvent);
            retval.setEcmEventType(EcmEventType.CREATE);
            retval.setUserId(createEvent.getString("user"));

            long auditId = createEvent.getLong("id");
            retval.setAuditId(auditId);

            retval.setNodeType(alfrescoTypeToArkCaseType.get(alfrescoContentType));

            String nodeInfo = values.getString("/auditarkcaseextractors/create/out/a");
            String[] nodeInfoArray = nodeInfo.split("\\|");
            String nodeId = nodeInfoArray[1];
            retval.setNodeId(nodeId);

            String parentNodeId = values.getString("/auditarkcaseextractors/create/in/a");
            retval.setParentNodeId(parentNodeId);

            String nodeName = values.getString("/auditarkcaseextractors/create/in/d");
            nodeName = nodeName.substring("{http://www.alfresco.org/model/content/1.0}".length());
            retval.setNodeName(nodeName);

            // "\/auditarkcaseextractors\/create\/out\/a":
            // "workspace:\/\/SpacesStore\/0d5add60-10ef-4369-bc87-f1f9b80fc448|workspace:\/\/SpacesStore\/682cd7df-3987-49da-b5c8-b54887406f08|{http:\/\/www.alfresco.org\/model\/content\/1.0}failedThumbnail|{http:\/\/www.alfresco.org\/model\/content\/1.0}doclib|true|-1",

            return retval;
        }

        return null;

    }
}
