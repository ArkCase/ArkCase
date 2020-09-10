package gov.foia.service;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
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
import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.portalgateway.service.PortalRequestServiceException;
import com.armedia.acm.portalgateway.service.PortalRequestServiceProvider;
import com.armedia.acm.portalgateway.web.api.PortalRequest;
import com.armedia.acm.portalgateway.web.api.PortalResponse;
import com.armedia.acm.services.pipeline.exception.PipelineProcessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import gov.foia.model.PortalFOIAInquiry;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import gov.foia.model.PortalFOIARequest;
import gov.foia.model.PortalFOIARequestStatus;

/**
 * @author Lazo Lazarev a.k.a. Lazarius Borg @ zerogravity Jun 8, 2018
 *
 */
public class FOIAPortalRequestServiceProvider implements PortalRequestServiceProvider
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private PortalCreateRequestService createRequestService;

    private PortalRequestService portalRequestService;

    private PortalCreateInquiryService portalCreateInquiryService;

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalRequestServiceProvider#providesServiceForRequestType()
     */
    @Override
    public String providesServiceForRequestType()
    {
        return PortalFOIARequest.class.getName();
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalRequestServiceProvider#submitRequest(java.lang.String,
     * com.armedia.acm.portalgateway.web.api.PortalRequest)
     */
    @Override
    public PortalResponse submitRequest(String portalId, String portalUserId, PortalRequest request) throws PortalRequestServiceException
    {
        log.debug("Submitting request from portal with [{}] ID for portal user with [{}] ID of [{}] type.", portalId, portalUserId,
                request.getRequestType());
        String rawRequestContent = request.getRawRequestContent();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            PortalFOIARequest foiaRequest = mapper.readValue(rawRequestContent, PortalFOIARequest.class);
            foiaRequest.setUserId(portalUserId);
            createRequestService.createFOIARequest(foiaRequest);
            return new PortalResponse();
        }
        catch (IOException e)
        {
            log.warn("Error deserializing raw request [{}] from user with ID [{}] from portal with ID [{}].", rawRequestContent,
                    portalUserId, portalId);
            throw new PortalRequestServiceException(String.format(
                    "Error deserializing raw request [%s] from user with ID [%s] from portal with ID [%s].", rawRequestContent,
                    portalUserId, portalId), e, SUBMIT_REQUEST_METHOD_DESERIALIZE);
        }
        catch (AcmCreateObjectFailedException | AcmUserActionFailedException | PipelineProcessException e)
        {
            log.warn("Error creating request for raw request [{}] from user with ID [{}] from portal with ID [{}].", rawRequestContent,
                    portalUserId, portalId);
            throw new PortalRequestServiceException(String.format(
                    "Error creating request for raw request [%s] from user with ID [%s] from portal with ID [%s].", rawRequestContent,
                    portalUserId, portalId), e, SUBMIT_REQUEST_METHOD_CREATE_REQUEST);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalRequestServiceProvider#listRequests(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<PortalResponse> listRequests(String portalId, String portalUserId) throws PortalRequestServiceException
    {
        try
        {
            List<PortalFOIARequestStatus> externalRequests = portalRequestService.getExternalRequests(portalUserId);
            return externalRequests.stream().map(this::mapRequestStatus).collect(Collectors.toList());
        }
        catch (AcmObjectNotFoundException e)
        {
            log.warn("Error fetching requests for user with ID [{}] from portal with ID [{}].", portalUserId, portalId);
            throw new PortalRequestServiceException(
                    String.format("Error fetching requests for user with ID [%s] from portal with ID [%s].", portalUserId, portalId), e,
                    LIST_REQUESTS_METHOD_RETRIEVE);
        }
        catch (ResponseMappingException e)
        {
            log.warn("Error serializing raw response due to [{}] from user with ID [{}] from portal with ID [{}].", e.getCause(),
                    portalUserId, portalId);
            throw new PortalRequestServiceException(String.format(
                    "Error serializing raw response due to [%s] from user with ID [%s] from portal with ID [%s].", e.getCause(),
                    portalUserId, portalId), e, LIST_REQUESTS_METHOD_SERIALIZE);
        }

    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalRequestServiceProvider#getRequestStatus(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public PortalResponse getRequestStatus(String portalId, String portalUserId, String requestId) throws PortalRequestServiceException
    {
        try
        {
            PortalFOIARequestStatus mapRequestStatus = portalRequestService.getExternalRequest(portalUserId, requestId);
            // TODO: this should be configurable
            // if ("Approved".equals(mapRequestStatus.getRequestStatus()))
            // {
            // // TODO: if a request processing was finished, we should return the result instead as part of
            // // PortalResponse#rawResponse
            // }
            return mapRequestStatus(mapRequestStatus);
        }
        catch (NoResultException | NonUniqueResultException e)
        {
            log.warn("Error fetching request with ID [{}] for user with ID [{}] from portal with ID [{}].", requestId, portalUserId,
                    portalId);
            throw new PortalRequestServiceException(String.format(
                    "Error fetching request with ID [%s] for user with ID [%s] from portal with ID [%s].", requestId, portalUserId,
                    portalId), e, GET_REQUEST_STATUS_METHOD_RETRIEVE);
        }
        catch (ResponseMappingException e)
        {
            log.warn("Error serializing raw response for request with ID [{}] due to [{}] from user with ID [{}] from portal with ID [{}].",
                    requestId, e.getCause(), portalUserId, portalId);
            throw new PortalRequestServiceException(String.format(
                    "Error serializing raw response for request with ID [%s] due to [%s}] from user with ID [%s] from portal with ID [%s].",
                    requestId, e.getCause(), portalUserId, portalId), e, GET_REQUEST_STATUS_METHOD_SERIALIZE);
        }
    }

    private PortalResponse mapRequestStatus(PortalFOIARequestStatus status)
    {
        try
        {
            ObjectMapper mapper = new ObjectMapper();
            PortalResponse rs = new PortalResponse();
            rs.setResponseType(PortalFOIARequestStatus.class.getName());
            rs.setRawResponse(mapper.writeValueAsString(status));
            return rs;
        }
        catch (JsonProcessingException e)
        {
            throw new ResponseMappingException(e);
        }
    }


    /*
     * (non-Javadoc)
     * @see com.armedia.acm.portalgateway.service.PortalRequestServiceProvider#submitInquiry(com.armedia.acm.portalgateway.web.api.PortalRequest)
     */
    @Override
    public void submitInquiry(PortalRequest request) throws PortalRequestServiceException
    {
        log.debug("Submitting request from portal with [{}] ID for portal user with [{}] ID of [{}] type.",
                request.getRequestType());
        String rawRequestContent = request.getRawRequestContent();
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            PortalFOIAInquiry foiaInquiry = mapper.readValue(rawRequestContent, PortalFOIAInquiry.class);
            portalCreateInquiryService.createFOIAInquiry(foiaInquiry);
        }
        catch (IOException e)
        {
            log.warn("Error deserializing raw request [{}] from user with ID [{}] from portal with ID [{}].", rawRequestContent);
            throw new PortalRequestServiceException(String.format(
                    "Error deserializing raw request [%s] from user with ID [%s] from portal with ID [%s].", rawRequestContent), e, SUBMIT_REQUEST_METHOD_DESERIALIZE);
        }

    }

    /**
     * @param createRequestService
     *            the createRequestService to set
     */
    public void setCreateRequestService(PortalCreateRequestService createRequestService)
    {
        this.createRequestService = createRequestService;
    }

    /**
     * @param portalRequestService
     *            the portalRequestService to set
     */
    public void setPortalRequestService(PortalRequestService portalRequestService)
    {
        this.portalRequestService = portalRequestService;
    }

    public PortalCreateInquiryService getPortalCreateInquiryService()
    {
        return portalCreateInquiryService;
    }

    public void setPortalCreateInquiryService(PortalCreateInquiryService portalCreateInquiryService)
    {
        this.portalCreateInquiryService = portalCreateInquiryService;
    }
}
