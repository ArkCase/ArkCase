package gov.foia.broker;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
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

import com.armedia.broker.AcmObjectBrokerClientHandler;
import com.armedia.broker.IAcmFileBrokerClient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import gov.foia.model.PortalFOIARequest;

/**
 * FOIARequestBrokerClient Integration Test
 *
 * @author dame.gjorgjievski
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-acm-encryption.xml",
        "classpath:/spring/spring-library-message-broker-test.xml"
})
public class FOIARequestBrokerClientTestIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
        System.setProperty("configuration.server.url", "http://localhost:9999");
    }

    private static final String TEST_FILE_CONTENT = "Test file {} content. XX";
    private static PortalFOIARequest inboundRequest;
    private static PortalFOIARequest outboundRequest;

    static
    {
        outboundRequest = new PortalFOIARequest();
        outboundRequest.setFirstName("Ark");
        outboundRequest.setLastName("Case");
        // outboundRequest.setCategory("Category");
        outboundRequest.setCity("Washington");
        outboundRequest.setCountry("USA");
        outboundRequest.setZip("23345");
        outboundRequest.setAddress1("Address");
        outboundRequest.setEmail("armedia@armedia.com");
        outboundRequest.setRequestType("Approval");
    }

    @Autowired
    ApplicationContext applicationContext;
    private FOIARequestBrokerClient broker;
    private IAcmFileBrokerClient fileBroker;

    @Test
    public void runBrokerObjectTests() throws Exception
    {
        broker = FOIARequestBrokerClient.class.cast(applicationContext.getBean("foiaRequestBrokerClient"));
        broker.setHandler(new AcmObjectBrokerClientHandler<PortalFOIARequest>()
        {
            @Override
            public boolean handleObject(PortalFOIARequest entity)
            {
                inboundRequest = entity;
                return true;
            }
        });
        testOutboundFOIARequest();
        testInboundFOIARequest();
    }

    @Test
    public void runBrokerFileTests() throws Exception
    {

        fileBroker = IAcmFileBrokerClient.class.cast(applicationContext.getBean("foiaRequestFileBrokerClient"));
        testOutboundFile();
        testInboundFile();
    }

    public void testOutboundFOIARequest() throws Exception
    {
        Assert.notNull(broker, "Broker is not initialized");
        System.out.println("Sending request " + outboundRequest);
        broker.sendEntity(outboundRequest);
    }

    public void testInboundFOIARequest() throws Exception
    {
        Thread.sleep(1000); // give it time for message receive
        System.out.println("Received portal request " + inboundRequest);
        Assert.notNull(inboundRequest, "No inbound request received");
        Assert.isTrue(inboundRequest.getEmail().equals(outboundRequest.getEmail()),
                "Received request email does not equal sent request email");
        Assert.isTrue(inboundRequest.getFirstName().equals(outboundRequest.getFirstName()),
                "Received request first name does not equal sent request first name");
        Assert.isTrue(inboundRequest.getLastName().equals(outboundRequest.getLastName()),
                "Received request last name does not equal sent request last name");
        Assert.isTrue(inboundRequest.getAddress1().equals(outboundRequest.getAddress1()),
                "Received request address does not equal sent request address");
    }

    public void testOutboundFile() throws Exception
    {
        Assert.notNull(fileBroker, "File Broker is not initialized");
        File testFile = File.createTempFile("testFile", ".txt");
        testFile.createNewFile();
        testFile.setWritable(true);
        Writer out = new FileWriter(testFile);
        out.write(TEST_FILE_CONTENT);
        out.close();
        System.out.println("Sending file " + testFile);
        fileBroker.sendFile(testFile, null);
        testFile.delete();
    }

    public void testInboundFile() throws Exception
    {
        Thread.sleep(1000); // give it time for message receive
        File inboundFile = fileBroker.receiveFile();
        System.out.println("Received portal file request " + inboundFile);
        Assert.notNull(inboundFile, "No inbound file received");
    }

}
