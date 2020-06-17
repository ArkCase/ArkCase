package com.armedia.acm.services.suggestion.web.api;

/*-
 * #%L
 * acm-service-suggestion
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

import com.armedia.acm.services.search.exception.SolrException;
import com.armedia.acm.services.suggestion.model.SuggestedObject;
import com.armedia.acm.services.suggestion.service.SimilarObjectsService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.ParseException;
import java.util.Base64;

@Controller
@RequestMapping({ "/api/v1/service/suggestion", "/api/latest/service/suggestion" })
public class GetSimilarObjectsAPIController
{

    private SimilarObjectsService similarObjectsService;

    @RequestMapping(value = "/{title}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SuggestedObject> findSimilarObjects(
            @PathVariable("title") String objectTitle,
            @RequestParam(value = "objectId", required = false) String objectId,
            @RequestParam(value = "objectType", required = false, defaultValue = "CASE_FILE") String objectType,
            @RequestParam(value = "portal", required = false, defaultValue = "false") Boolean isPortal,
            Authentication authentication) throws ParseException, SolrException
    {
        Long id = null;
        if (objectId != null)
        {
            id = Long.valueOf(objectId);
        }

        // we need to decode base64 encoded title because can contain characters which can interfere with url
        objectTitle = new String(Base64.getUrlDecoder().decode(objectTitle.getBytes()));

        return new ResponseEntity(getSimilarObjectsService().findSimilarObjects(objectTitle, objectType, isPortal, id, authentication),
                HttpStatus.OK);
    }

    public SimilarObjectsService getSimilarObjectsService()
    {
        return similarObjectsService;
    }

    public void setSimilarObjectsService(SimilarObjectsService similarObjectsService)
    {
        this.similarObjectsService = similarObjectsService;
    }
}
