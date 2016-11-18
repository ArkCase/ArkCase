package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public class GetTicketService extends AlfrescoService<String>
{
    private static final String KERBEROS_USERNAME_PREFIX = "KERBEROS/";

    private final static int EXPECTED_TICKET_LENGTH = 47;
    private final static String OPEN_TICKET_TAG = "<ticket>";
    private final static String END_TICKET_TAG = "</ticket>";

    private TicketEntityService ticketEntityService;

    private String user;
    private String password;

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    @Override
    public String doService(Map<String, Object> context) throws AlfrescoServiceException
    {
        String base = baseUrl();

        try
        {
            ResponseEntity<String> response = getTicketEntityService().getEntity(base, user, password);

            if (!HttpStatus.OK.equals(response.getStatusCode()))
            {
                LOG.error("Received response {}.  Response body: [{}]", response.getStatusCode(), response.getBody());
                throw new AlfrescoServiceException(response.getStatusCode() + " received in service " + getClass().getName());
            }

            String ticketXml = response.getBody();

            int openTagIndex = ticketXml.indexOf(OPEN_TICKET_TAG);
            int endTagIndex = ticketXml.indexOf(END_TICKET_TAG);
            String ticket = ticketXml.substring(openTagIndex + OPEN_TICKET_TAG.length(), endTagIndex);

            validateResponse(ticket);

            return ticket;
        }
        catch (RestClientException rce)
        {
            throw new AlfrescoServiceException(rce.getMessage(), rce);
        }

    }

    private void validateResponse(String ticket) throws AlfrescoServiceException
    {
        if (ticket == null || !ticket.startsWith("TICKET_") || EXPECTED_TICKET_LENGTH != ticket.length())
        {
            throw new AlfrescoServiceException("Unexpected ticket format: " + ticket);
        }
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public synchronized TicketEntityService getTicketEntityService()
    {
        if (ticketEntityService == null)
        {
            if (getUser().startsWith(KERBEROS_USERNAME_PREFIX))
            {
                ticketEntityService = new KerberosTicketEntityService();
                // remove KERBEROS/ prefix from username
                user = user.trim().substring(KERBEROS_USERNAME_PREFIX.length());
            }
            else
            {
                ticketEntityService = new BasicAuthTicketEntityService();
            }
        }
        return ticketEntityService;
    }
}
