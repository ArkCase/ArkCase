package com.armedia.acm.plugins.ecm.service.sync;

import com.armedia.acm.plugins.ecm.model.sync.EcmCreateEvent;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoFileFolderServiceAuditResponseReader;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by dmiller on 5/12/17.
 */
public class AlfrescoFileFolderServiceAuditResponseReaderTest
{

    private JSONObject fileFolderServiceAuditResponseJson;
    private CreateNodesResponseReader unit = new AlfrescoFileFolderServiceAuditResponseReader();

    @Before
    public void setUp() throws Exception
    {
        final Resource fileFolderServiceAuditResponseResource = new ClassPathResource("json/SampleAlfrescoFileFolderServiceAuditResponse.json");
        String fileFolderServiceAuditResponseString = FileUtils.readFileToString(fileFolderServiceAuditResponseResource.getFile());
        fileFolderServiceAuditResponseJson = new JSONObject(fileFolderServiceAuditResponseString);
    }

    @Test
    public void readResponse() throws Exception
    {
        List<EcmCreateEvent> createEvents = unit.read(fileFolderServiceAuditResponseJson);

        assertNotNull(createEvents);
        assertEquals(2, createEvents.size());


        Object[][] expectedData = {
                // userid, parent node type, parent node id, node type, node id, file name
                {"admin", "folder", "workspace://SpacesStore/4d1b5ed8-7544-4cf6-acc0-384756138f95", "document", "workspace://SpacesStore/0af7fe58-885d-4f01-a9e6-0ebf994abb3f", "ArkCase - Infrastructure Team - Scrum - 2017-05-05.docx"},
                {"admin", "folder", "workspace://SpacesStore/78d7455d-c2d3-43f8-a160-94827e6b3c3a", "document", "workspace://SpacesStore/0fd810e4-eb46-4b87-8951-8aa339dc150a", "ArkCase - Infrastructure Team - Scrum - 2017-05-03.docx"},
        };

        int index = 0;
        for (EcmCreateEvent ece : createEvents)
        {
            assertEquals("User #" + index, expectedData[index][0], ece.getUserId());
            assertEquals("parentNodeType #" + index, expectedData[index][1], ece.getParentNodeType());
            assertEquals("parentNodeId #" + index, expectedData[index][2], ece.getParentNodeId());
            assertEquals("nodeType #" + index, expectedData[index][3], ece.getNodeType());
            assertEquals("nodeId #" + index, expectedData[index][4], ece.getNodeId());
            assertEquals("nodeName #" + index, expectedData[index][5], ece.getNodeName());

            ++index;
        }
    }
}
