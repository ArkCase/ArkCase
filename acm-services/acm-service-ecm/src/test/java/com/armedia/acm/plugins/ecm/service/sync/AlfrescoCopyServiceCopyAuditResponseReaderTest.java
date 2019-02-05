package com.armedia.acm.plugins.ecm.service.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoCopyServiceCopyAuditResponseReader;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @author ivana.shekerova on 1/4/2019.
 */
public class AlfrescoCopyServiceCopyAuditResponseReaderTest
{

    private JSONObject copyServiceCopyAuditResponseJson;
    private EcmAuditResponseReader unit = new AlfrescoCopyServiceCopyAuditResponseReader();

    @Before
    public void setUp() throws Exception
    {
        final Resource copyServiceCopyAuditResponseResource = new ClassPathResource(
                "json/SampleAlfrescoCopyServiceCopyAuditResponse.json");
        String copyServiceCopyAuditResponseString = FileUtils
                .readFileToString(copyServiceCopyAuditResponseResource.getFile());
        copyServiceCopyAuditResponseJson = new JSONObject(copyServiceCopyAuditResponseString);
    }

    @Test
    public void readResponse()
    {
        List<EcmEvent> copyEvents = unit.read(copyServiceCopyAuditResponseJson);

        assertNotNull(copyEvents);
        assertEquals(1, copyEvents.size());

        Object[][] expectedData = {
                // event type, auditId, userid, parent node type, parent node id, node type, node id, file name
                { EcmEventType.COPY, 26624L, "admin", "folder", "workspace://SpacesStore/a341ac50-b60b-4bd8-b1b3-922dcd05a0a7",
                        "document", "workspace://SpacesStore/3d887e96-3166-4404-a428-2fd12d6c4a81",
                        "doc.txt" }
        };

        int index = 0;
        for (EcmEvent ece : copyEvents)
        {
            assertEquals("Event type #" + index, expectedData[index][0], ece.getEcmEventType());
            assertEquals("Audit ID #" + index, expectedData[index][1], ece.getAuditId());
            assertEquals("User #" + index, expectedData[index][2], ece.getUserId());
            assertEquals("ParentNodeType #" + index, expectedData[index][3], ece.getParentNodeType());
            assertEquals("ParentNodeId #" + index, expectedData[index][4], ece.getParentNodeId());
            assertEquals("NodeType #" + index, expectedData[index][5], ece.getNodeType());
            assertEquals("NodeId #" + index, expectedData[index][6], ece.getNodeId());
            assertEquals("NodeName #" + index, expectedData[index][7], ece.getNodeName());

            ++index;
        }
    }
}
