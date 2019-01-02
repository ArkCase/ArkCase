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
 * Read Alfresco audit records from the Alfresco FileFolderService <code>moveFrom</code> method. Alfresco uses the
 * FileFolderService move method to move content files (but NOT to move folders, or thumbnails).
 * <p>
 * For now, from the file/folder service we are interested only in moved content files. This reader ignores all other
 * new content types.
 *
 * @author ivana.shekerova on 12/14/2018.
 */
public class AlfrescoFileFolderServiceMoveAuditResponseReader implements EcmAuditResponseReader
{
    protected final Map<String, String> alfrescoTypeToArkCaseType = ImmutableMap
            .of("{http://www.alfresco.org/model/content/1.0}content", EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT,
                    "{http://www.alfresco.org/model/content/1.0}folder", EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);

    protected final List<String> typesToIncludeInResults = Arrays.asList(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT);

    @Override
    public EcmEvent buildEcmEvent(JSONObject moveEvent)
    {
        // this reader only cares about documents
        JSONObject values = moveEvent.getJSONObject("values");

        String alfrescoContentType = values.getString("/auditarkcasemovefrom/move/derived/source-node-type");
        String arkcaseContentType = alfrescoTypeToArkCaseType.get(alfrescoContentType);
        boolean includeThisNode = typesToIncludeInResults.contains(arkcaseContentType);

        if (includeThisNode)
        {
            EcmEvent retval = new EcmEvent(moveEvent);
            retval.setEcmEventType(EcmEventType.MOVE);
            retval.setUserId(moveEvent.getString("user"));

            long auditId = moveEvent.getLong("id");
            retval.setAuditId(auditId);

            retval.setNodeType(alfrescoTypeToArkCaseType.get(alfrescoContentType));

            String nodeInfo = values.getString("/auditarkcasemovefrom/move/out/a");

            String nodeId = extractNodeId(nodeInfo);
            retval.setNodeId(nodeId);

            String nodeName = extractNodeName(nodeInfo);
            retval.setNodeName(nodeName);

            String alfrescoSourceParentNodeType = values.getString("/auditarkcasemovefrom/move/derived/source-parent-type");
            String sourceParentNodeType = alfrescoTypeToArkCaseType.get(alfrescoSourceParentNodeType);
            retval.setSourceParentNodeType(sourceParentNodeType);

            String sourceParentNodeId = values.getString("/auditarkcasemovefrom/move/in/b");
            retval.setSourceParentNodeId(sourceParentNodeId);

            String alfrescoTargetParentNodeType = values.getString("/auditarkcasemovefrom/move/derived/target-parent-type");
            String targetParentNodeType = alfrescoTypeToArkCaseType.get(alfrescoTargetParentNodeType);
            retval.setTargetParentNodeType(targetParentNodeType);

            String targetParentNodeId = values.getString("/auditarkcasemovefrom/move/in/c");
            retval.setTargetParentNodeId(targetParentNodeId);

            return retval;
        }

        return null;

    }
}
