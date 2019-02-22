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
 * Read Alfresco audit records from the Alfresco FileFolderService <code>rename</code> method. Alfresco uses the
 * FileFolderService rename method to rename content files and folders.
 *
 * @author ivana.shekerova on 1/16/2019.
 */
public class AlfrescoFileFolderServiceRenameAuditResponseReader implements EcmAuditResponseReader
{
    protected final Map<String, String> alfrescoTypeToArkCaseType = ImmutableMap
            .of("{http://www.alfresco.org/model/content/1.0}content", EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT,
                    "{http://www.alfresco.org/model/content/1.0}folder", EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);

    protected final List<String> typesToIncludeInResults = Arrays.asList(EcmFileConstants.ECM_SYNC_NODE_TYPE_DOCUMENT, EcmFileConstants.ECM_SYNC_NODE_TYPE_FOLDER);

    @Override
    public EcmEvent buildEcmEvent(JSONObject createEvent)
    {
        // this reader only cares about documents
        JSONObject values = createEvent.getJSONObject("values");

        String alfrescoContentType = values.getString("/auditarkcaserename/rename/derived/node-type");
        String arkcaseContentType = alfrescoTypeToArkCaseType.get(alfrescoContentType);
        boolean includeThisNode = typesToIncludeInResults.contains(arkcaseContentType);

        if (includeThisNode)
        {
            EcmEvent retval = new EcmEvent(createEvent);
            retval.setEcmEventType(EcmEventType.RENAME);
            retval.setUserId(createEvent.getString("user"));

            long auditId = createEvent.getLong("id");
            retval.setAuditId(auditId);

            retval.setNodeType(alfrescoTypeToArkCaseType.get(alfrescoContentType));

            String nodeId = values.getString("/auditarkcaserename/rename/in/a");
            retval.setNodeId(nodeId);

            String newNodeName = values.getString("/auditarkcaserename/rename/in/b");
            retval.setNodeName(newNodeName);

            return retval;
        }

        return null;

    }
}
