package gov.foia.web.api;

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

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;

import org.json.JSONException;
import org.mule.api.MuleException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.List;

import gov.foia.model.PortalFOIAReadingRoom;
import gov.foia.model.PortalFOIARequest;
import gov.foia.model.PortalFOIARequestStatus;
import gov.foia.service.PortalRequestService;

/**
 * @author sasko.tanaskoski
 *
 */
@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class PortalRequestAPIController
{
    private final Logger log = LogManager.getLogger(getClass());

    private PortalRequestService portalRequestService;

    @RequestMapping(value = "/external/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PortalFOIARequestStatus> getExternalRequests(PortalFOIARequestStatus requestStatus)
            throws UnsupportedEncodingException, MuleException, AcmObjectNotFoundException
    {
        return getPortalRequestService().getExternalRequests(requestStatus);
    }

    @RequestMapping(value = "/external/readingroom", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<PortalFOIAReadingRoom> getReadingRoom(PortalFOIAReadingRoom readingRoom, Authentication auth)
            throws UnsupportedEncodingException, MuleException, AcmObjectNotFoundException, JSONException, ParseException
    {
        return getPortalRequestService().getReadingRoom(readingRoom, auth);
    }

    @RequestMapping(value = "/external/checkRequestStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PortalFOIARequest checkRequestStatus(PortalFOIARequest portalFOIARequest) throws JSONException
    {
        return getPortalRequestService().checkRequestStatus(portalFOIARequest);
    }

    @RequestMapping(value = "/external/requestDownloadTriggered/{requestId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> requestDownloadTriggered(@PathVariable("requestId") String requestNumber)
    {
        try
        {
            getPortalRequestService().sendRequestDownloadedEmailToOfficersGroup(requestNumber);
            return new ResponseEntity<>(true, HttpStatus.OK);
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @return the portalRequestService
     */
    public PortalRequestService getPortalRequestService()
    {
        return portalRequestService;
    }

    /**
     * @param portalRequestService
     *            the portalRequestService to set
     */
    public void setPortalRequestService(PortalRequestService portalRequestService)
    {
        this.portalRequestService = portalRequestService;
    }

}
