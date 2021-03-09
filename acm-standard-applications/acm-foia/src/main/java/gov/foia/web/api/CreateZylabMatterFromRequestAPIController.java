package gov.foia.web.api;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2021 ArkCase LLC
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

import gov.foia.model.FOIARequest;
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

import gov.foia.service.FOIAZylabMatterService;

@Controller
@RequestMapping({ "/api/v1/plugin/casefile", "/api/latest/plugin/casefile" })
public class CreateZylabMatterFromRequestAPIController
{
    private transient final Logger log = LogManager.getLogger(getClass());

    private FOIAZylabMatterService foiaZylabMatterService;

    @PreAuthorize("hasPermission(#requestId, 'CASE_FILE', 'saveCase')")
    @RequestMapping(value = "/{requestId}/createZylabMatter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity createMatterFromRequest(@PathVariable("requestId") Long requestId, Authentication authentication)
    {
        try
        {
            log.debug("Creating ZyLAB Matter for request [{}]", requestId);
            FOIARequest request = getFoiaZylabMatterService().createMatterFromRequest(requestId);
            return new ResponseEntity<>(request, HttpStatus.OK);
        }
        catch (Exception e)
        {
            log.error("Failed to create a ZyLAB Matter for request [{}]", requestId, e);
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public FOIAZylabMatterService getFoiaZylabMatterService()
    {
        return foiaZylabMatterService;
    }

    public void setFoiaZylabMatterService(FOIAZylabMatterService foiaZylabMatterService)
    {
        this.foiaZylabMatterService = foiaZylabMatterService;
    }
}
