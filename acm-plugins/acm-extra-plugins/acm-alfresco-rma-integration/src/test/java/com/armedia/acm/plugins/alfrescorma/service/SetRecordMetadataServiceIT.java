package com.armedia.acm.plugins.alfrescorma.service;

/*-
 * #%L
 * ACM Extra Plugin: Alfresco RMA Integration
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

import com.armedia.acm.camelcontext.context.CamelContextManager;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.web.api.MDCConstants;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-alfresco-records-service-test.xml",
        "/spring/spring-library-alfresco-service.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-camel-context.xml",
        "/spring/spring-library-property-file-manager.xml"
})
public class SetRecordMetadataServiceIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }
    private transient final Logger LOG = LogManager.getLogger(getClass());
    @Autowired
    private CamelContextManager camelContextManager;
    @Autowired
    @Qualifier("declareRecordService")
    private AlfrescoService<String> declareRecordService;
    @Autowired
    @Qualifier("setRecordMetadataService")
    private AlfrescoService<String> service;
    private String ecmFileId;
    private CmisFileWriter cmisFileWriter = new CmisFileWriter();

    @Before
    public void setUp() throws Exception
    {
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ALFRESCO_USER_ID_KEY, "admin");
        MDC.put(MDCConstants.EVENT_MDC_REQUEST_ID_KEY, UUID.randomUUID().toString());

        Document testFile = cmisFileWriter.writeTestFile(camelContextManager);
        ecmFileId = testFile.getProperty(EcmFileConstants.REPOSITORY_VERSION_ID).getValue();
    }

    @Test
    public void setRecordMetadat() throws Exception
    {
        assertNotNull(declareRecordService);

        Map<String, Object> declareRecordContext = new HashMap<>();

        declareRecordContext.put("ecmFileId", ecmFileId);

        String actedOnId = declareRecordService.service(declareRecordContext);

        assertEquals(ecmFileId, actedOnId);

        Map<String, Object> context = new HashMap<>();
        context.put("ecmFileId", ecmFileId);
        context.put("publicationDate", new Date());
        context.put("originator", "Jerry Garcia");
        context.put("originatingOrganization", "Grateful Dead");
        context.put("dateReceived", new Date());

        String metadataId = service.service(context);
        assertEquals(ecmFileId, metadataId);

    }
}
