package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.muletools.mulecontextmanager.MuleContextManager;
import org.apache.chemistry.opencmis.client.api.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-alfresco-records-service-test.xml",
        "/spring/spring-library-alfresco-service.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-property-file-manager.xml"
})
public class DeclareRecordServiceIT
{
    @Autowired
    private MuleContextManager muleContextManager;

    @Autowired
    @Qualifier("declareRecordService")
    private AlfrescoService<String> service;

    @Autowired
    @Qualifier("alfrescoGetTicketService")
    private AlfrescoService<String> ticketService;

    private String ecmFileId;

    private CmisFileWriter cmisFileWriter = new CmisFileWriter();

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Before
    public void setUp() throws Exception
    {
        Document testFile = cmisFileWriter.writeTestFile(muleContextManager);
        ecmFileId = testFile.getVersionSeriesId();
    }

    @Test
    public void declareRecord() throws Exception
    {
        assertNotNull(service);

        String ticket = ticketService.service(null);

        Map<String, Object> context = new HashMap<>();

        context.put("ecmFileId", ecmFileId);
        context.put("ticket", ticket);

        String retval = service.service(context);

        assertEquals(ecmFileId, retval);


    }
}
