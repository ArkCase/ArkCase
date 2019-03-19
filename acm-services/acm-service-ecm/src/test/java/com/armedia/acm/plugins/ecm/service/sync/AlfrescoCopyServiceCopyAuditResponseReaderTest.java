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
        assertEquals(2, copyEvents.size());

        Object[][] expectedData = {
                // event type, auditId, userid, parent node type, parent node id, node type, node id, file name
                { EcmEventType.COPY, 26627L, "admin", "folder", "workspace://SpacesStore/a341ac50-b60b-4bd8-b1b3-922dcd05a0a7",
                        "folder", "workspace://SpacesStore/c78612c1-6fbd-4123-a51a-775890d91e9b",
                        "subfolder" },
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
