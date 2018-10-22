package gov.foia.broker;

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
@ContextConfiguration(locations = { "classpath:/spring/spring-library-message-broker-test.xml" })
public class FOIARequestBrokerClientTestIT
{

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
