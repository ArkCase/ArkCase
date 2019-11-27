package com.armedia.acm.plugins.ecm.web.api;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.service.StreamService;

import org.apache.catalina.connector.ClientAbortException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private final Logger LOG = LogManager.getLogger(getClass());

    private StreamService streamService;

    @PreAuthorize("hasPermission(#id, 'FILE', 'read|group-read|write|group-write')")
    @RequestMapping(value = "/stream/{id}", method = RequestMethod.GET)
    @ResponseBody
    public void stream(@PathVariable(value = "id") Long id,
            @RequestParam(value = "version", required = false, defaultValue = "") String version, Authentication authentication,
            HttpServletRequest request, HttpServletResponse response)
            throws IOException, AcmObjectNotFoundException, AcmUserActionFailedException
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
