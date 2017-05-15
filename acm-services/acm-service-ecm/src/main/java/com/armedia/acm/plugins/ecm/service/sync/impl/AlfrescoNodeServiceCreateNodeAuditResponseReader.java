package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.sync.EcmAuditResponseReader;
import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
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
public class AlfrescoNodeServiceCreateNodeAuditResponseReader implements EcmAuditResponseReader
{
    private final Map<String, String> alfrescoTypeToArkCaseType = ImmutableMap.of(
            "{http://www.alfresco.org/model/content/1.0}folder", "folder"
    );

    @Override
    public List<EcmEvent> read(JSONObject createNodesJson)
    {
        int count = createNodesJson.getInt("count");

        JSONArray auditEvents = createNodesJson.getJSONArray("entries");

        List<EcmEvent> events = IntStream.range(0, count)
                .mapToObj(auditEvents::getJSONObject)
                .map(this::buildEcmCreateEvent)
                .filter(Objects::nonNull)
                .collect(toList());

        return events;
    }

    protected EcmEvent buildEcmCreateEvent(JSONObject createEvent)
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

            // "\/auditarkcaseextractors\/create\/out\/a": "workspace:\/\/SpacesStore\/0d5add60-10ef-4369-bc87-f1f9b80fc448|workspace:\/\/SpacesStore\/682cd7df-3987-49da-b5c8-b54887406f08|{http:\/\/www.alfresco.org\/model\/content\/1.0}failedThumbnail|{http:\/\/www.alfresco.org\/model\/content\/1.0}doclib|true|-1",

            return retval;
        }

        return null;

    }
}
