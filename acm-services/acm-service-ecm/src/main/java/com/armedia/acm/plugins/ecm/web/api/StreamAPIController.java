package com.armedia.acm.plugins.ecm.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.service.StreamService;

import org.apache.catalina.connector.ClientAbortException;
import org.mule.api.MuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Created by riste.tutureski on 6/5/2017.
 */
@Controller
@RequestMapping({ "/api/v1/plugin/ecm", "/api/latest/plugin/ecm" })
public class StreamAPIController
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private StreamService streamService;

    @PreAuthorize("hasPermission(#id, 'FILE', 'read')")
    @RequestMapping(value = "/stream/{id}", method = RequestMethod.GET)
    @ResponseBody
    public void stream(@PathVariable(value = "id") Long id,
            @RequestParam(value = "version", required = false, defaultValue = "") String version, Authentication authentication,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, MuleException, AcmObjectNotFoundException, AcmUserActionFailedException
    {
        LOG.info("Streaming file with ID '{}' for user '{}'", id, authentication.getName());

        try
        {
            getStreamService().stream(id, version, request, response);
        }
        catch (ClientAbortException e)
        {
            LOG.debug("The connection is terminated by the client");
            // Do nothing. The connection is closed by the client and writing to the output should be terminated
        }
    }

    public StreamService getStreamService()
    {
        return streamService;
    }

    public void setStreamService(StreamService streamService)
    {
        this.streamService = streamService;
    }
}
