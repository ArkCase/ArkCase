package com.armedia.acm.plugins.ecm.service.sync.impl;

import com.armedia.acm.plugins.ecm.model.sync.EcmCreateEvent;
import com.armedia.acm.plugins.ecm.service.sync.CreateNodesResponseReader;
import com.google.common.collect.ImmutableMap;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/**
 * Read Alfresco audit records from the Alfresco FileFolderService <code>create</code> method.  Alfresco uses the
 * FileFolderService create method to create content files (but NOT to create folders, or thumbnails... Alfresco uses
 * the NodeService to create folders and thumbnails).
 * <p>
 * For now, from the file/folder service we are interested only in new content files.  This reader ignores all other new
 * content types.
 */
public class AlfrescoFileFolderServiceCreateAuditResponseReader implements CreateNodesResponseReader
{
    protected final Map<String, String> alfrescoTypeToArkCaseType = ImmutableMap.of(
            "{http://www.alfresco.org/model/content/1.0}content", "document",
            "{http://www.alfresco.org/model/content/1.0}folder", "folder"
    );

    protected final List<String> typesToIncludeInResults = Arrays.asList("document");

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
        // this reader only cares about documents
        JSONObject values = createEvent.getJSONObject("values");

        String alfrescoContentType = values.getString("/auditarkcasecreate/create/in/c");
        String arkcaseContentType = alfrescoTypeToArkCaseType.get(alfrescoContentType);
        boolean includeThisNode = typesToIncludeInResults.contains(arkcaseContentType);

        if (includeThisNode)
        {

            EcmCreateEvent retval = new EcmCreateEvent(createEvent);
            retval.setUserId(createEvent.getString("user"));

            retval.setNodeType(alfrescoTypeToArkCaseType.get(alfrescoContentType));

            // FileInfo[name=ArkCase - Infrastructure Team - Scrum - 2017-05-05.docx, isFolder=false, nodeRef=workspace:\/\/SpacesStore\/0af7fe58-885d-4f01-a9e6-0ebf994abb3f]
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

    private String extractNodeName(String nodeInfo)
    {
        int nameIdx = nodeInfo.indexOf("name=");
        int startIdx = nameIdx + "name=".length();
        int endIdx = nodeInfo.lastIndexOf(", isFolder");
        return nodeInfo.substring(startIdx, endIdx);
    }

    private String extractNodeId(String nodeInfo)
    {
        int nodeRefIdx = nodeInfo.lastIndexOf("nodeRef=");
        int startIdx = nodeRefIdx + "nodeRef=".length();
        int endIdx = nodeInfo.lastIndexOf("]");
        return nodeInfo.substring(startIdx, endIdx);
    }
}
