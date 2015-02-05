package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.plugins.ecm.model.EcmFile;
import com.armedia.acm.plugins.objectassociation.model.ObjectAssociation;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mule.api.MuleMessage;
import org.mule.api.client.MuleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-search-service-test-content-file-mule.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-cmis-configuration.xml"
})
public class ContentFileToSolrFlowIT
{
    @Autowired
    private MuleClient muleClient;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * To run this test, just find a CMIS Object ID you want to test (by looking at your Alfresco installation),
     * and plug that into the EcmFileId.
     * @throws Exception
     */
    @Test
    @Ignore
    public void sendFile() throws Exception
    {
        EcmFile testFile = new EcmFile();

        DateFormat solrDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String created = solrDateFormat.format(new Date());
        Date date = solrDateFormat.parse(created);

        ObjectAssociation oa = new ObjectAssociation();
        oa.setParentId(22222L);
        oa.setParentName("PARENT-PARENT-PARENT");
        oa.setParentType("COMPLAINT");

        testFile.setEcmFileId("workspace://SpacesStore/2b697afd-6e7a-474b-bf75-5fadcb29fa84");
        testFile.setFileName("Clearance Denied 2015130-230216-658.docx");
        testFile.setFileMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        testFile.setFileId(4444444L);
        testFile.setCreated(date);
        testFile.setModified(date);
        testFile.setCreator("ann-acm");
        testFile.setModifier("marjan-acm");
        testFile.addParentObject(oa);

        Map<String, Object> headers = new HashMap<>();

        MuleMessage response = muleClient.send("jms://solrContentFile.in", testFile, headers);

        assertTrue(response.getPayload() != null && response.getPayload() instanceof String);

        assertNull(response.getExceptionPayload());

        log.debug("response: " + response.getPayloadAsString());


    }
}
