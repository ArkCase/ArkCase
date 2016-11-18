package com.armedia.acm.plugins.alfrescorma.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;

/**
 * Service that issues Alfresco service call to get alf_ticket.
 * <p>
 * Created by bojan.milenkoski on 10.11.2016
 */
public interface TicketEntityService
{
    /**
     * Issues service call to Alfresco ticket service and returns the {@link ResponseEntity} returned by the server.
     *
     * @param baseUrl
     *            the base URL to Alfresco web application
     * @param user
     *            the username that is used to get the alf_ticket for
     * @param password
     *            the user' password
     * @return the {@link ResponseEntity} returned by the server
     * @throws RestClientException
     *             when client side error occurs
     */
    ResponseEntity<String> getEntity(String baseUrl, String user, String password) throws RestClientException;
}
