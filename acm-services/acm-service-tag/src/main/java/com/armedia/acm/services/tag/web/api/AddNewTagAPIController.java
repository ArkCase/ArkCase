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

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.TagEventPublisher;
import com.armedia.acm.services.tag.service.TagService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/tag", "/api/latest/service/tag" })
public class AddNewTagAPIController
{

    private transient final Logger log = LogManager.getLogger(getClass());
    private TagService tagService;
    private TagEventPublisher tagEventPublisher;

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTag addNewTag(
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "desc", required = true) String desc,
            @RequestParam(value = "text", required = true) String text,
            Authentication authentication) throws AcmCreateObjectFailedException
    {

        if (log.isInfoEnabled())
        {
            log.info("Creating new tag with text:" + text + " description: " + desc + " name: " + name);
        }
        AcmTag returnedTag = getTagService().getTagByTextOrDescOrName(text, desc, name);

        if (returnedTag != null)
        {
            if (log.isDebugEnabled())
            {
                log.debug("Tag with id: " + returnedTag.getId() + " name: " + name + " or description: " + desc + " or text:" + text
                        + " already exists");
            }
            throw new AcmCreateObjectFailedException(AcmTag.OBJECT_TYPE, "Tag with Name: " + name + " or Description: " + desc
                    + " or Text: " + text + " already exists in the DB, pls try to delete or update existing one", null);
        }
        else
        {
            AcmTag addedTag = null;
            try
            {
                addedTag = getTagService().saveTag(name, desc, text);
                getTagEventPublisher().publishTagCreatedEvent(addedTag, authentication, true);
                return addedTag;
            }
            catch (Exception e)
            {
                if (log.isErrorEnabled())
                    log.error("Exception occurred while trying to insert new Tag into DB with name: " + name + " and Description: " + desc
                            + " and Text: " + text, e);
                getTagEventPublisher().publishTagCreatedEvent(addedTag, authentication, false);
                throw new AcmCreateObjectFailedException(AcmTag.OBJECT_TYPE, "Tag with Name: " + name + " or Description: " + desc
                        + " or Text: " + text + " was not inserted into DB due to exception", e);
            }
        }
    }

    public TagEventPublisher getTagEventPublisher()
    {
        return tagEventPublisher;
    }

    public void setTagEventPublisher(TagEventPublisher tagEventPublisher)
    {
        this.tagEventPublisher = tagEventPublisher;
    }

    public TagService getTagService()
    {
        return tagService;
    }

    public void setTagService(TagService tagService)
    {
        this.tagService = tagService;
    }

}
