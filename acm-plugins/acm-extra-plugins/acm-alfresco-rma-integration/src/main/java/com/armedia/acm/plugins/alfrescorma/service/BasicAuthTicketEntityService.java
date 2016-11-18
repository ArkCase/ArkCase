package com.armedia.acm.plugins.alfrescorma.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Implementation of {@link TicketEntityService} that uses HTTP Basic authentication for the Alfresco ticket service call.
 * <p>
 * Created by bojan.milenkoski on 10.11.2016
 */
public class BasicAuthTicketEntityService implements TicketEntityService
{
    private RestTemplate restTemplate = new RestTemplate();
    private final String service = "s/api/login";
    private final String query = "u={user}" + "&" + "pw={password}";

    @Override
    public ResponseEntity<String> getEntity(String baseUrl, String user, String password) throws RestClientException
    {
        String ticketUrl = baseUrl + "/" + service + "?" + query;

        return restTemplate.getForEntity(ticketUrl, String.class, user, password);
    }
}
