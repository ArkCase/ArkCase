package com.armedia.acm.services.tag.web.api;

/*-
 * #%L
 * ACM Service: Tag
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
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.AssociatedTagService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 27.03.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/tag", "/api/latest/service/tag" })
public class ListAssociatedTagsByObjectTypeAndIdAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private AssociatedTagService associatedTagService;

    @RequestMapping(value = "/{objectType}/{objectId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmTag> listAssociatedTaqsBy(
            @PathVariable("objectType") String objectType,
            @PathVariable("objectId") Long objectId,
            Authentication auth) throws AcmUserActionFailedException, AcmObjectNotFoundException
    {

        log.info("Listing assigned tags for objectType: {} and objectId: {}", objectType, objectId);
        List<AcmAssociatedTag> acmAssociatedTags;
        List<AcmTag> acmTags;
        try
        {
            acmAssociatedTags = getAssociatedTagService().getAcmAssociatedTagsByObjectIdAndType(objectId, objectType, auth);
            acmTags = retrieveTagList(acmAssociatedTags);
        }
        catch (AcmObjectNotFoundException e)
        {
            log.debug("No Associated Tags are Found for objectId: {} and objectType: {}", objectId, objectType);
            return new ArrayList<>();
        }
        return acmTags;
    }

    private List<AcmTag> retrieveTagList(List<AcmAssociatedTag> acmAssociatedTags)
    {
        return acmAssociatedTags.stream().map(s -> s.getTag()).collect(Collectors.toList());
    }

    public AssociatedTagService getAssociatedTagService()
    {
        return associatedTagService;
    }

    public void setAssociatedTagService(AssociatedTagService associatedTagService)
    {
        this.associatedTagService = associatedTagService;
    }
}
