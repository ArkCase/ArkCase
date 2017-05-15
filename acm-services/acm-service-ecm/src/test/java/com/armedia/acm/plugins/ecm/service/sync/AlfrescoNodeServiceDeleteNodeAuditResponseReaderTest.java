package com.armedia.acm.plugins.ecm.service.sync;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoNodeServiceDeleteNodeAuditResponseReader;
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
public class AlfrescoNodeServiceDeleteNodeAuditResponseReaderTest
{

    private JSONObject alfrescoNodeServiceDeleteNodeAuditResponseJson;
    private EcmAuditResponseReader unit = new AlfrescoNodeServiceDeleteNodeAuditResponseReader();

    @Before
    public void setUp() throws Exception
    {
        final Resource alfrescoNodeServiceDeleteNodeAuditResponseResource = new ClassPathResource("json/SampleAlfrescoNodeServiceDeleteNodeAuditResponse.json");
        String deleteNodesAuditResponseString = FileUtils.readFileToString(alfrescoNodeServiceDeleteNodeAuditResponseResource.getFile());
        alfrescoNodeServiceDeleteNodeAuditResponseJson = new JSONObject(deleteNodesAuditResponseString);
    }

    @Test
    public void readResponse() throws Exception
    {
        List<EcmEvent> deleteEvents = unit.read(alfrescoNodeServiceDeleteNodeAuditResponseJson);

        assertNotNull(deleteEvents);
        assertEquals(3, deleteEvents.size());


        Object[][] expectedData = {
                // event type, audit id, userid, node id
                {EcmEventType.DELETE, 44L, "admin", "workspace://SpacesStore/faf7562e-0731-4dfc-8b94-36a2f5df0f0d"},
                {EcmEventType.DELETE, 54L, "admin", "workspace://SpacesStore/b021e743-3d85-4ff9-8c0b-cc318376ee70"},
                {EcmEventType.DELETE, 62L, "admin", "workspace://SpacesStore/b2f2bde5-2499-48ac-8bab-907e6d031e58"}
        };

        int index = 0;
        for (EcmEvent ede : deleteEvents)
        {
            assertEquals("Event type #" + index, expectedData[index][0], ede.getEcmEventType());
            assertEquals("audit id #" + index, expectedData[index][1], ede.getAuditId());
            assertEquals("User #" + index, expectedData[index][2], ede.getUserId());
            assertEquals("nodeId #" + index, expectedData[index][3], ede.getNodeId());

            ++index;
        }
    }
}
