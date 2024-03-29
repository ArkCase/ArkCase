package com.armedia.acm.plugins.ecm.service.sync;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoNodeServiceCreateNodeAuditResponseReader;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * Created by dmiller on 5/12/17.
 */
public class AlfrescoNodeServiceCreateNodeAuditResponseReaderTest
{

    private JSONObject alfrescoNodeServiceCreateNodeAuditResponseJson;
    private AlfrescoNodeServiceCreateNodeAuditResponseReader unit = new AlfrescoNodeServiceCreateNodeAuditResponseReader();

    @Before
    public void setUp() throws Exception
    {
        final Resource alfrescoNodeServiceCreateNodeAuditResponseResource = new ClassPathResource(
                "json/SampleAlfrescoNodeServiceCreateNodeAuditResponse.json");
        String createNodesAuditResponseString = FileUtils.readFileToString(alfrescoNodeServiceCreateNodeAuditResponseResource.getFile());
        alfrescoNodeServiceCreateNodeAuditResponseJson = new JSONObject(createNodesAuditResponseString);
    }

    @Test
    public void readResponse() throws Exception
    {
        List<EcmEvent> createEvents = unit.read(alfrescoNodeServiceCreateNodeAuditResponseJson);

        assertNotNull(createEvents);
        assertEquals(3, createEvents.size());

        Object[][] expectedData = {
                // userid, audit id, node type, node id, parent node id, node name
                { EcmEventType.CREATE, 42L, "admin", "folder", "workspace://SpacesStore/faf7562e-0731-4dfc-8b94-36a2f5df0f0d",
                        "workspace://SpacesStore/2e535fb5-078d-4b84-99d5-fe23ce8e88a7", "Grateful Dead" },
                { EcmEventType.CREATE, 52L, "admin", "folder", "workspace://SpacesStore/b021e743-3d85-4ff9-8c0b-cc318376ee70",
                        "workspace://SpacesStore/2e535fb5-078d-4b84-99d5-fe23ce8e88a7", "Jerry Garcia" },
                { EcmEventType.CREATE, 57L, "admin", "folder", "workspace://SpacesStore/4d1b5ed8-7544-4cf6-acc0-384756138f95",
                        "workspace://SpacesStore/2e535fb5-078d-4b84-99d5-fe23ce8e88a7", "Documents" }
        };

        int index = 0;
        for (EcmEvent ece : createEvents)
        {
            assertEquals("Event type #" + index, expectedData[index][0], ece.getEcmEventType());
            assertEquals("Audit ID #" + index, expectedData[index][1], ece.getAuditId());
            assertEquals("User #" + index, expectedData[index][2], ece.getUserId());
            assertEquals("nodeType #" + index, expectedData[index][3], ece.getNodeType());
            assertEquals("nodeId #" + index, expectedData[index][4], ece.getNodeId());
            assertEquals("parentNodeId #" + index, expectedData[index][5], ece.getParentNodeId());
            assertEquals("nodeName #" + index, expectedData[index][6], ece.getNodeName());

            ++index;
        }
    }
}
