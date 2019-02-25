package com.armedia.acm.plugins.ecm.service.sync;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoFileFolderServiceMoveAuditResponseReader;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @author ivana.shekerova on 12/19/2018.
 */
public class AlfrescoFileFolderServiceMoveAuditResponseReaderTest
{

    private JSONObject fileFolderServiceMoveAuditResponseJson;
    private EcmAuditResponseReader unit = new AlfrescoFileFolderServiceMoveAuditResponseReader();

    @Before
    public void setUp() throws Exception
    {
        final Resource fileFolderServiceMoveAuditResponseResource = new ClassPathResource(
                "json/SampleAlfrescoFileFolderServiceMoveAuditResponse.json");
        String fileFolderServiceMoveAuditResponseString = FileUtils
                .readFileToString(fileFolderServiceMoveAuditResponseResource.getFile());
        fileFolderServiceMoveAuditResponseJson = new JSONObject(fileFolderServiceMoveAuditResponseString);
    }

    @Test
    public void readResponse()
    {
        List<EcmEvent> moveEvents = unit.read(fileFolderServiceMoveAuditResponseJson);

        assertNotNull(moveEvents);
        assertEquals(2, moveEvents.size());

        Object[][] expectedData = {
                // event type, auditId, userid, parent node type, parent node id, node type, node id, file name
                { EcmEventType.MOVE, 25307L, "admin", "folder", "workspace://SpacesStore/5c133b31-dd84-42bd-841f-048bc9202dc3",
                        "folder", "workspace://SpacesStore/a341ac50-b60b-4bd8-b1b3-922dcd05a0a7",
                        "document", "workspace://SpacesStore/de9236ca-1e75-4d31-b3bc-15eb30faecae",
                        "New Microsoft Word Document.docx" },
                { EcmEventType.MOVE, 25306L, "admin", "folder", "workspace://SpacesStore/a341ac50-b60b-4bd8-b1b3-922dcd05a0a7",
                        "folder", "workspace://SpacesStore/5c133b31-dd84-42bd-841f-048bc9202dc3",
                        "folder", "workspace://SpacesStore/fa3128c0-4ce6-497e-8375-7254c3efd218",
                        "subfolder" }
        };

        int index = 0;
        for (EcmEvent ece : moveEvents)
        {
            assertEquals("Event type #" + index, expectedData[index][0], ece.getEcmEventType());
            assertEquals("Audit ID #" + index, expectedData[index][1], ece.getAuditId());
            assertEquals("User #" + index, expectedData[index][2], ece.getUserId());
            assertEquals("SourceParentNodeType #" + index, expectedData[index][3], ece.getSourceParentNodeType());
            assertEquals("SourceParentNodeId #" + index, expectedData[index][4], ece.getSourceParentNodeId());
            assertEquals("TargetParentNodeType #" + index, expectedData[index][5], ece.getTargetParentNodeType());
            assertEquals("TargetParentNodeId #" + index, expectedData[index][6], ece.getTargetParentNodeId());
            assertEquals("NodeType #" + index, expectedData[index][7], ece.getNodeType());
            assertEquals("NodeId #" + index, expectedData[index][8], ece.getNodeId());
            assertEquals("NodeName #" + index, expectedData[index][9], ece.getNodeName());

            ++index;
        }
    }
}
