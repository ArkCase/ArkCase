package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.plugins.ecm.model.sync.EcmCreateEvent;
import com.armedia.acm.plugins.ecm.service.sync.CreateNodesResponseReader;
import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Created by dmiller on 5/12/17.
 */
public class AlfrescoCreateFoldersAuditResponseReader implements CreateNodesResponseReader
{
    private final Map<String, String> alfrescoTypeToArkCaseType = ImmutableMap.of(
            "{http://www.alfresco.org/model/content/1.0}folder", "folder"
    );

    @Override
    public List<EcmCreateEvent> read(JSONObject createNodesJson)
    {
        int count = createNodesJson.getInt("count");

        JSONArray auditEvents = createNodesJson.getJSONArray("entries");

        List<EcmCreateEvent> events = IntStream.range(0, count)
                .mapToObj(auditEvents::getJSONObject)
                .map(this::buildEcmCreateEvent)
                .filter(Objects::nonNull)
                .collect(toList());

        return events;
    }

    protected EcmCreateEvent buildEcmCreateEvent(JSONObject createEvent)
    {
        // this reader only cares about folders
        JSONObject values = createEvent.getJSONObject("values");

        String alfrescoContentType = values.getString("/auditarkcaseextractors/create/in/b");

        if (alfrescoTypeToArkCaseType.containsKey(alfrescoContentType))
        {

            EcmCreateEvent retval = new EcmCreateEvent(createEvent);
            retval.setUserId(createEvent.getString("user"));

            retval.setEventProperties(new HashMap<>());
            retval.getEventProperties().put("nodeType", alfrescoTypeToArkCaseType.get(alfrescoContentType));

            String nodeInfo = values.getString("/auditarkcaseextractors/create/out/a");
            String[] nodeInfoArray = nodeInfo.split("\\|");
            String nodeId = nodeInfoArray[1];
            retval.getEventProperties().put("nodeId", nodeId);

            String parentNodeId = values.getString("/auditarkcaseextractors/create/in/a");
            retval.getEventProperties().put("parentNodeId", parentNodeId);

            String nodeName = values.getString("/auditarkcaseextractors/create/in/d");
            nodeName = nodeName.substring("{http://www.alfresco.org/model/content/1.0}".length());
            retval.getEventProperties().put("nodeName", nodeName);

            // "\/auditarkcaseextractors\/create\/out\/a": "workspace:\/\/SpacesStore\/0d5add60-10ef-4369-bc87-f1f9b80fc448|workspace:\/\/SpacesStore\/682cd7df-3987-49da-b5c8-b54887406f08|{http:\/\/www.alfresco.org\/model\/content\/1.0}failedThumbnail|{http:\/\/www.alfresco.org\/model\/content\/1.0}doclib|true|-1",

            return retval;
        }

        return null;

    }
}
