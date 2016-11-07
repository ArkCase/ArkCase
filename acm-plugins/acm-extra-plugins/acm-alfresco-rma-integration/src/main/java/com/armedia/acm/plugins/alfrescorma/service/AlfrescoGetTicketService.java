package com.armedia.acm.plugins.alfrescorma.service;

import com.armedia.acm.plugins.alfrescorma.exception.AlfrescoServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by dmiller on 11/7/2016.
 */
public class AlfrescoGetTicketService extends AlfrescoService<String>
{
    private final int EXPECTED_TICKET_LENGTH = 47;
    private final int START_TAG_OFFSET = 7;
    private final int END_TAG_OFFSET = 10;
    private final RestTemplate restTemplate;

    private String user;
    private String password;

    private final String service = "/s/api/login";
    private final String query = "u={user}" + "&" + "pw={password}";

    private transient final Logger LOG = LoggerFactory.getLogger(getClass());

    public AlfrescoGetTicketService()
    {
        restTemplate = new RestTemplate();
    }


    @Override
    public String doService(Map<String, Object> context) throws AlfrescoServiceException
    {
        String base = baseUrl();

        String ticketUrl = base + "/" + service + "?" + query;

        try
        {
            ResponseEntity<String> response = restTemplate.getForEntity(ticketUrl, String.class, user, password);

            if (!HttpStatus.OK.equals(response.getStatusCode()))
            {
                LOG.error("Received response {}.  Response body: [{}]", response.getStatusCode(), response.getBody());
                throw new AlfrescoServiceException(response.getStatusCode() + " received in service " + getClass().getName());
            }

            String ticketXml = response.getBody();

            int openTagIndex = ticketXml.indexOf("ticket");
            int length = ticketXml.length();
            String ticket = ticketXml.substring(openTagIndex + START_TAG_OFFSET, length - END_TAG_OFFSET);

            validateResponse(ticket);

            return ticket;
        } catch (RestClientException rce)
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

}
