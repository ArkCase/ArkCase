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
 * Read Alfresco audit records from the Alfresco CopyService <code>copyAndRename</code> method. Alfresco uses the
 * CopyService copyAndRename method to copy content files (but NOT to copy folders, or thumbnails).
 * 
 * For now, from the file/folder service we are interested only in copied content files. This reader ignores all other
 * new content types.
 *
 * @author ivana.shekerova on 1/2/2019.
 */
public class AlfrescoCopyServiceCopyAuditResponseReader implements EcmAuditResponseReader
{
    protected final Map<String, String> alfrescoTypeToArkCaseType = ImmutableMap
            .of("{http://www.alfresco.org/model/content/1.0}content", EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT,
                    "{http://www.alfresco.org/model/content/1.0}folder", EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);

    protected final List<String> typesToIncludeInResults = Arrays.asList(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT);

    @Override
    public EcmEvent buildEcmEvent(JSONObject copyEvent)
    {
        // this reader only cares about documents
        JSONObject values = copyEvent.getJSONObject("values");

        String alfrescoContentType = values.getString("/auditarkcasecopy/copy/derived/source-node-type");
        String arkcaseContentType = alfrescoTypeToArkCaseType.get(alfrescoContentType);
        boolean includeThisNode = typesToIncludeInResults.contains(arkcaseContentType);

        if (includeThisNode)
        {
            EcmEvent retval = new EcmEvent(copyEvent);
            retval.setEcmEventType(EcmEventType.COPY);
            retval.setUserId(copyEvent.getString("user"));

            long auditId = copyEvent.getLong("id");
            retval.setAuditId(auditId);

            retval.setNodeType(arkcaseContentType);

            String nodeId = values.getString("/auditarkcasecopy/copy/out/a");
            retval.setNodeId(nodeId);

            String nodeName = values.getString("/auditarkcasecopy/copy/derived/source-node-name");
            retval.setNodeName(nodeName);

            String alfrescoParentNodeType = values.getString("/auditarkcasecopy/copy/derived/target-parent-type");
            retval.setParentNodeType(alfrescoTypeToArkCaseType.get(alfrescoParentNodeType));

            String parentNodeId = values.getString("/auditarkcasecopy/copy/in/b");
            retval.setParentNodeId(parentNodeId);

            String sourceOfCopyNodeId = values.getString("/auditarkcasecopy/copy/in/a");
            retval.setSourceOfCopyNodeId(sourceOfCopyNodeId);

            return retval;
        }

        return null;

    }
}
