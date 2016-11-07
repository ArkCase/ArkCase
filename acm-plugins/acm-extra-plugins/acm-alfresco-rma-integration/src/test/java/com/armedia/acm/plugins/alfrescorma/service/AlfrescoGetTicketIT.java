package com.armedia.acm.plugins.alfrescorma.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-alfresco-service.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-property-file-manager.xml"
})
public class AlfrescoGetTicketIT
{
    @Autowired
    @Qualifier("alfrescoGetTicketService")
    private AlfrescoService<String> service;

    @Test
    public void getTicket() throws Exception
    {
        assertNotNull(service);

        String ticket = service.service(null);

        System.out.println("Ticket: " + ticket);

        assertTrue(ticket.startsWith("TICKET_"));

        assertEquals(47, ticket.length());

    }
}
