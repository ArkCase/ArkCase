package com.armedia.acm.plugins.ecm.service.sync;

import com.armedia.acm.plugins.ecm.model.sync.EcmDeleteEvent;
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
    private DeleteNodesResponseReader unit = new AlfrescoNodeServiceDeleteNodeAuditResponseReader();

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
        List<EcmDeleteEvent> deleteEvents = unit.read(alfrescoNodeServiceDeleteNodeAuditResponseJson);

        assertNotNull(deleteEvents);
        assertEquals(3, deleteEvents.size());


        Object[][] expectedData = {
                // userid, node id
                {"admin", "workspace://SpacesStore/faf7562e-0731-4dfc-8b94-36a2f5df0f0d"},
                {"admin", "workspace://SpacesStore/b021e743-3d85-4ff9-8c0b-cc318376ee70"},
                {"admin", "workspace://SpacesStore/b2f2bde5-2499-48ac-8bab-907e6d031e58"}
        };

        int index = 0;
        for (EcmDeleteEvent ede : deleteEvents)
        {
            assertEquals("User #" + index, expectedData[index][0], ede.getUserId());
            assertEquals("nodeId #" + index, expectedData[index][1], ede.getNodeId());

            ++index;
        }
    }
}
