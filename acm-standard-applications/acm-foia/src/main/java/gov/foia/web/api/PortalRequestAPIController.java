package gov.foia.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;

import org.json.JSONException;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
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
    private final Logger log = LoggerFactory.getLogger(getClass());

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
