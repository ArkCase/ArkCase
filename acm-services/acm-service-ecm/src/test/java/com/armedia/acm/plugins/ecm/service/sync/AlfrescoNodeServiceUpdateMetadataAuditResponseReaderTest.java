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
import static org.junit.Assert.assertTrue;

import com.armedia.acm.plugins.ecm.model.sync.EcmEvent;
import com.armedia.acm.plugins.ecm.model.sync.EcmEventType;
import com.armedia.acm.plugins.ecm.service.sync.impl.AlfrescoNodeServiceUpdateMetadataAuditResponseReader;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Map;

/**
 * @author ivana.shekerova on 1/21/2019.
 */
public class AlfrescoNodeServiceUpdateMetadataAuditResponseReaderTest
{

    private JSONObject alfrescoNodeServiceUpdateMetadataAuditResponseJson;
    private AlfrescoNodeServiceUpdateMetadataAuditResponseReader unit = new AlfrescoNodeServiceUpdateMetadataAuditResponseReader();

    @Before
    public void setUp() throws Exception
    {
        final Resource alfrescoNodeServiceUpdateMetadataAuditResponseResource = new ClassPathResource(
                "json/SampleAlfrescoNodeServiceUpdateMetadataAuditResponse.json");
        String updateMetadataAuditResponseString = FileUtils
                .readFileToString(alfrescoNodeServiceUpdateMetadataAuditResponseResource.getFile());
        alfrescoNodeServiceUpdateMetadataAuditResponseJson = new JSONObject(updateMetadataAuditResponseString);
    }

    @Test
    public void readResponse()
    {
        List<EcmEvent> updateMetadataEvents = unit.read(alfrescoNodeServiceUpdateMetadataAuditResponseJson);

        assertNotNull(updateMetadataEvents);
        assertEquals(3, updateMetadataEvents.size());

        Object[][] expectedData = {
                // event type, auditId, userid, node type, node id, node name
                { EcmEventType.UPDATE, 27345L, "admin", "document", "workspace://SpacesStore/a4069180-39fa-4ef5-a94e-3ead1e20fa99",
                        "docNew3.txt" },
                { EcmEventType.UPDATE, 27340L, "admin", "document", "workspace://SpacesStore/8f8ba21d-b027-48d7-9b95-0ac2cdbdfac3",
                        "Casefile" },
                { EcmEventType.UPDATE, 27718L, "admin", "folder", "workspace://SpacesStore/93dfae54-498d-4cb6-a2f9-a4c4caa44029",
                        "pictures" }
        };
        
        String[] properties = {"{{http://www.alfresco.org/model/content/1.0}title=, {http://www.alfresco.org/model/content/1.0}taggable=null, {http://www.alfresco.org/model/content/1.0}description=opiiiiiis}",
        "{{http://www.alfresco.org/model/content/1.0}title=Title for the case, {http://www.alfresco.org/model/content/1.0}taggable=null, {http://www.alfresco.org/model/content/1.0}description=opppppiiiiiissss}",
        "{{http://www.alfresco.org/model/content/1.0}title=, {http://www.alfresco.org/model/content/1.0}taggable=null, {http://www.alfresco.org/model/content/1.0}description=contains pictures related to the Casefile}"};

        int index = 0;
        for (EcmEvent ece : updateMetadataEvents)
        {
            assertEquals("Event type #" + index, expectedData[index][0], ece.getEcmEventType());
            assertEquals("Audit ID #" + index, expectedData[index][1], ece.getAuditId());
            assertEquals("User #" + index, expectedData[index][2], ece.getUserId());
            assertEquals("nodeType #" + index, expectedData[index][3], ece.getNodeType());
            assertEquals("nodeId #" + index, expectedData[index][4], ece.getNodeId());
            assertEquals("nodeName #" + index, expectedData[index][5], ece.getNodeName());

            Map<String ,String> propertiesMap = unit.mapProperties(properties[index]);
            assertTrue(propertiesMap.equals(ece.getProperties()));
            ++index;
        }
    }
}
