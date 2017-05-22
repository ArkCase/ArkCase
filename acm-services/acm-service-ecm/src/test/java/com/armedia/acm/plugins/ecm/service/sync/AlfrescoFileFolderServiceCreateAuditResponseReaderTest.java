package com.armedia.acm.plugins.ecm.service.sync;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoFileFolderServiceCreateAuditResponseReader;
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
public class AlfrescoFileFolderServiceCreateAuditResponseReaderTest
{

    private JSONObject fileFolderServiceCreateAuditResponseJson;
    private EcmAuditResponseReader unit = new AlfrescoFileFolderServiceCreateAuditResponseReader();

    @Before
    public void setUp() throws Exception
    {
        final Resource fileFolderServiceCreateAuditResponseResource = new ClassPathResource("json/SampleAlfrescoFileFolderServiceCreateAuditResponse.json");
        String fileFolderServiceCreateAuditResponseString = FileUtils.readFileToString(fileFolderServiceCreateAuditResponseResource.getFile());
        fileFolderServiceCreateAuditResponseJson = new JSONObject(fileFolderServiceCreateAuditResponseString);
    }

    @Test
    public void readResponse() throws Exception
    {
        List<EcmEvent> createEvents = unit.read(fileFolderServiceCreateAuditResponseJson);

        assertNotNull(createEvents);
        assertEquals(2, createEvents.size());


        Object[][] expectedData = {
                // event type, auditId, userid, parent node type, parent node id, node type, node id, file name
                {EcmEventType.CREATE, 85L, "admin", "folder", "workspace://SpacesStore/4d1b5ed8-7544-4cf6-acc0-384756138f95", "document", "workspace://SpacesStore/0af7fe58-885d-4f01-a9e6-0ebf994abb3f", "ArkCase - Infrastructure Team - Scrum - 2017-05-05.docx"},
                {EcmEventType.CREATE, 91L, "admin", "folder", "workspace://SpacesStore/78d7455d-c2d3-43f8-a160-94827e6b3c3a", "document", "workspace://SpacesStore/0fd810e4-eb46-4b87-8951-8aa339dc150a", "ArkCase - Infrastructure Team - Scrum - 2017-05-03.docx"},
        };

        int index = 0;
        for (EcmEvent ece : createEvents)
        {
            assertEquals("Event type #" + index, expectedData[index][0], ece.getEcmEventType());
            assertEquals("Audit ID #" + index, expectedData[index][1], ece.getAuditId());
            assertEquals("User #" + index, expectedData[index][2], ece.getUserId());
            assertEquals("parentNodeType #" + index, expectedData[index][3], ece.getParentNodeType());
            assertEquals("parentNodeId #" + index, expectedData[index][4], ece.getParentNodeId());
            assertEquals("nodeType #" + index, expectedData[index][5], ece.getNodeType());
            assertEquals("nodeId #" + index, expectedData[index][6], ece.getNodeId());
            assertEquals("nodeName #" + index, expectedData[index][7], ece.getNodeName());

            ++index;
        }
    }
}
