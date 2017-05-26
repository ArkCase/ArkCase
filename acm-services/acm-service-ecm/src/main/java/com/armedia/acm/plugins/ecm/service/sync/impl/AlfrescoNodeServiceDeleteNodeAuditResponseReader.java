package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.sync.EcmAuditResponseReader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Read Alfresco audit records generated from the Alfresco NodeService createNode method.  Alfresco uses the NodeService
 * to create folders, and also to create thumbnails and some other objects (but NOT to create content files... Alfresco
 * uses the FileFolderService to create content files).
 * <p>
 * For now, from the node service we are interested only in new folders.  This reader ignores all other new
 * content types.
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
