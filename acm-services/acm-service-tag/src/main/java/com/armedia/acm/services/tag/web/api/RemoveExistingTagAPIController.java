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
import com.armedia.acm.services.tag.service.TagEventPublisher;
import com.armedia.acm.services.tag.service.TagService;

import org.activiti.engine.impl.util.json.JSONObject;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

import java.sql.SQLException;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/tag", "/api/latest/service/tag" })
public class RemoveExistingTagAPIController
{

    private final static String SUCCESS_MSG = "Tag removed successfully: ";
    private final static String USER_ACTION = "DELETE";
    private transient final Logger log = LogManager.getLogger(getClass());
    private TagService tagService;
    private TagEventPublisher tagEventPublisher;

    @RequestMapping(value = "/{tagId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String removeTag(
            @PathVariable("tagId") Long tagId,
            Authentication authentication,
            HttpSession httpSession) throws AcmUserActionFailedException
    {
        if (log.isInfoEnabled())
        {
            log.info("Removing tag with tagId: " + tagId);
        }
        try
        {
            AcmTag tag = tagService.findTag(tagId);
            if (tag != null)
            {
                getTagService().removeTag(tag);
                if (log.isDebugEnabled())
                    log.debug("Tag with tagId:" + tagId + "  successfully removed");
                getTagEventPublisher().publishTagDeletedEvent(tag, authentication, true);
                return prepareJsonReturnMsg(SUCCESS_MSG, tagId);
            }
            else
            {
                if (log.isDebugEnabled())
                    log.debug("Tag with tagId:" + tagId + " not found in the DB");
                getTagEventPublisher().publishTagDeletedEvent(tag, authentication, false);
                return prepareJsonReturnMsg(SUCCESS_MSG, tagId);
            }
        }
        catch (SQLException e)
        {
            if (log.isErrorEnabled())
                log.error("SQL Exception was thrown while deleting tag with tagId: " + tagId);
            throw new AcmUserActionFailedException(USER_ACTION, AcmTag.OBJECT_TYPE, tagId, "SQL Exception was thrown while deleting tag",
                    e);
        }
    }

    private String prepareJsonReturnMsg(String msg, Long tagId)
    {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedTagId", tagId);
        objectToReturnJSON.put("Message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
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
