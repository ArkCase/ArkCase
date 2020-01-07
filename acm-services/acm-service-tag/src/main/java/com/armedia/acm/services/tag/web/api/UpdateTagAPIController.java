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

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.model.AcmTagDto;
import com.armedia.acm.services.tag.service.TagEventPublisher;
import com.armedia.acm.services.tag.service.TagService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLException;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */

@Controller
@RequestMapping({ "/api/v1/service/tag", "/api/latest/service/tag" })
public class UpdateTagAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private TagService tagService;
    private TagEventPublisher tagEventPublisher;

    @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTag updateNewTag(
            @RequestBody AcmTagDto in,
            Authentication authentication) throws AcmUserActionFailedException
    {

        if (log.isInfoEnabled())
        {
            log.info("Update tag: " + in.getTagId() + "  with text:" + in.getTagText() + " description: " + in.getTagDescription()
                    + "and name: " + in.getTagName());
        }

        AcmTag tagForUpdate = getTagService().findTag(in.getTagId());
        AcmTag updatedTag;
        try
        {
            updatedTag = getTagService().updateTag(in.getTagId(), in.getTagName(), in.getTagText(), in.getTagDescription());
            getTagEventPublisher().publishTagUpdatedEvent(updatedTag, authentication, true);
        }
        catch (SQLException e)
        {
            if (log.isErrorEnabled())
                log.error("Exception occurred while updating tag with a tagId: " + in.getTagId(), e);
            getTagEventPublisher().publishTagUpdatedEvent(tagForUpdate, authentication, false);
            throw new AcmUserActionFailedException("Update a Tag", AcmTag.OBJECT_TYPE, in.getTagId(), "Updating tag failed!", e);
        }
        return updatedTag;
    }

    public TagService getTagService()
    {
        return tagService;
    }

    public void setTagService(TagService tagService)
    {
        this.tagService = tagService;
    }

    public TagEventPublisher getTagEventPublisher()
    {
        return tagEventPublisher;
    }

    public void setTagEventPublisher(TagEventPublisher tagEventPublisher)
    {
        this.tagEventPublisher = tagEventPublisher;
    }
}
