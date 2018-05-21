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
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoFileFolderServiceCreateAuditResponseReader;

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
public class AlfrescoFileFolderServiceCreateAuditResponseReaderTest
{

    private JSONObject fileFolderServiceCreateAuditResponseJson;
    private EcmAuditResponseReader unit = new AlfrescoFileFolderServiceCreateAuditResponseReader();

    @Before
    public void setUp() throws Exception
    {
        final Resource fileFolderServiceCreateAuditResponseResource = new ClassPathResource(
                "json/SampleAlfrescoFileFolderServiceCreateAuditResponse.json");
        String fileFolderServiceCreateAuditResponseString = FileUtils
                .readFileToString(fileFolderServiceCreateAuditResponseResource.getFile());
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
                { EcmEventType.CREATE, 55L, "admin", "folder", "workspace://SpacesStore/4d1b5ed8-7544-4cf6-acc0-384756138f95", "document",
                        "workspace://SpacesStore/0af7fe58-885d-4f01-a9e6-0ebf994abb3f",
                        "ArkCase - Infrastructure Team - Scrum - 2017-05-05.docx" },
                { EcmEventType.CREATE, 91L, "admin", "folder", "workspace://SpacesStore/78d7455d-c2d3-43f8-a160-94827e6b3c3a", "document",
                        "workspace://SpacesStore/0fd810e4-eb46-4b87-8951-8aa339dc150a",
                        "ArkCase - Infrastructure Team - Scrum - 2017-05-03.docx" },
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
