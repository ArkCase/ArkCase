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

import com.armedia.acm.convertfolder.ConversionException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.ecm.exception.AcmFolderException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import gov.foia.service.RequestResponseFolderService;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class RequestResponseFolderAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private RequestResponseFolderService requestResponseFolderService;

    @PreAuthorize("hasPermission(#requestId, 'CASE_FILE', 'saveCase')")
    @RequestMapping(value = "/{caseId}/compressAndSendResponseFolder", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity compressAndSendResponseFolderToPortal(@PathVariable("caseId") Long requestId, Authentication authentication)
            throws AcmObjectNotFoundException, AcmUserActionFailedException, ConversionException, AcmFolderException
    {
        try
        {
            log.debug("Trying to Compress and Send the Response folder for the request [{}] to Portal", requestId);
            getRequestResponseFolderService().compressAndSendResponseFolderToPortal(requestId, authentication.getName());
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (ConversionException | AcmUserActionFailedException | AcmFolderException | AcmObjectNotFoundException e)
        {
            log.error("Failed to Compress and Send the Response folder for the request [{}] to Portal", requestId, e);
            throw e;
        }
    }

    public RequestResponseFolderService getRequestResponseFolderService()
    {
        return requestResponseFolderService;
    }

    public void setRequestResponseFolderService(RequestResponseFolderService requestResponseFolderService)
    {
        this.requestResponseFolderService = requestResponseFolderService;
    }
}
