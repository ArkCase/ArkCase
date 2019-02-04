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
import java.util.List;
import java.util.Map;

/**
 * Read Alfresco audit records from the Alfresco FileFolderService <code>create</code> method. Alfresco uses the
 * FileFolderService create method to create content files (but NOT to create folders, or thumbnails... Alfresco uses
 * the NodeService to create folders and thumbnails).
 * <p>
 * For now, from the file/folder service we are interested only in new content files. This reader ignores all other new
 * content types.
 */
public class AlfrescoFileFolderServiceCreateAuditResponseReader implements EcmAuditResponseReader
{
    protected final Map<String, String> alfrescoTypeToArkCaseType = ImmutableMap
            .of("{http://www.alfresco.org/model/content/1.0}content", EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT,
                    "{http://www.alfresco.org/model/content/1.0}folder", EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);

    protected final List<String> typesToIncludeInResults = Arrays.asList(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT);

    @Override
    public EcmEvent buildEcmEvent(JSONObject createEvent)
    {
        // this reader only cares about documents
        JSONObject values = createEvent.getJSONObject("values");

        String alfrescoContentType = values.getString("/auditarkcasecreate/create/in/c");
        String arkcaseContentType = alfrescoTypeToArkCaseType.get(alfrescoContentType);
        boolean includeThisNode = typesToIncludeInResults.contains(arkcaseContentType);

        if (includeThisNode)
        {
            EcmEvent retval = new EcmEvent(createEvent);
            retval.setEcmEventType(EcmEventType.CREATE);
            retval.setUserId(createEvent.getString("user"));

            long auditId = createEvent.getLong("id");
            retval.setAuditId(auditId);

            retval.setNodeType(alfrescoTypeToArkCaseType.get(alfrescoContentType));

            // FileInfo[name=ArkCase - Infrastructure Team - Scrum - 2017-05-05.docx, isFolder=false,
            // nodeRef=workspace:\/\/SpacesStore\/0af7fe58-885d-4f01-a9e6-0ebf994abb3f]
            String nodeInfo = values.getString("/auditarkcasecreate/create/out/a");

            String nodeId = extractNodeId(nodeInfo);
            retval.setNodeId(nodeId);

            String nodeName = extractNodeName(nodeInfo);
            retval.setNodeName(nodeName);

            String alfrescoParentNodeType = values.getString("/auditarkcasecreate/create/derived/parent-node-type");
            String parentNodeType = alfrescoTypeToArkCaseType.get(alfrescoParentNodeType);
            retval.setParentNodeType(parentNodeType);

            String parentNodeId = values.getString("/auditarkcasecreate/create/in/a");
            retval.setParentNodeId(parentNodeId);

            return retval;
        }

        return null;

    }
}
