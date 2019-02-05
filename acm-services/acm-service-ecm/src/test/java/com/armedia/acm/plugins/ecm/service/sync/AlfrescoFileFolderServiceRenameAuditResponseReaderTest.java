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
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoFileFolderServiceRenameAuditResponseReader;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * @author ivana.shekerova on 1/16/2019.
 */
public class AlfrescoFileFolderServiceRenameAuditResponseReaderTest
{

    private JSONObject fileFolderServiceRenameAuditResponseJson;
    private EcmAuditResponseReader unit = new AlfrescoFileFolderServiceRenameAuditResponseReader();

    @Before
    public void setUp() throws Exception
    {
        final Resource fileFolderServiceRenameAuditResponseResource = new ClassPathResource(
                "json/SampleAlfrescoFileFolderServiceRenameAuditResponse.json");
        String fileFolderServiceRenameAuditResponseString = FileUtils
                .readFileToString(fileFolderServiceRenameAuditResponseResource.getFile());
        fileFolderServiceRenameAuditResponseJson = new JSONObject(fileFolderServiceRenameAuditResponseString);
    }

    @Test
    public void readResponse()
    {
        List<EcmEvent> moveEvents = unit.read(fileFolderServiceRenameAuditResponseJson);

        assertNotNull(moveEvents);
        assertEquals(2, moveEvents.size());

        Object[][] expectedData = {
                // event type, auditId, userid, node type, node id,  file name
                { EcmEventType.RENAME, 27229L, "admin", "folder", "workspace://SpacesStore/93dfae54-498d-4cb6-a2f9-a4c4caa44029",
                        "pictures" },
                { EcmEventType.RENAME, 27228L, "admin", "document", "workspace://SpacesStore/a8b179d5-e1e8-46fd-9d87-c53c5f7d1535",
                        "word.docx" }
        };

        int index = 0;
        for (EcmEvent ece : moveEvents)
        {
            assertEquals("Event type #" + index, expectedData[index][0], ece.getEcmEventType());
            assertEquals("Audit ID #" + index, expectedData[index][1], ece.getAuditId());
            assertEquals("User #" + index, expectedData[index][2], ece.getUserId());
            assertEquals("NodeType #" + index, expectedData[index][3], ece.getNodeType());
            assertEquals("NodeId #" + index, expectedData[index][4], ece.getNodeId());
            assertEquals("NodeName #" + index, expectedData[index][5], ece.getNodeName());

            ++index;
        }
    }
}
