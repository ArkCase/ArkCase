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
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.service.AssociatedTagEventPublisher;
import com.armedia.acm.services.tag.service.AssociatedTagService;

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

import java.sql.SQLException;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/tag", "/api/latest/service/tag" })
public class RemoveTagAssociationAPIController
{

    private final static String SUCCESS_MSG = "Associated Tag Removed Successfully";
    private final static int NO_ROW_DELETED = 0;
    private transient final Logger log = LogManager.getLogger(getClass());
    private AssociatedTagService associatedTagService;
    private AssociatedTagEventPublisher associatedTagEventPublisher;

    @RequestMapping(value = "/{objectId}/{objectType}/{tagId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteTag(
            @PathVariable("objectId") Long objectId,
            @PathVariable("objectType") String objectType,
            @PathVariable("tagId") Long tagId,
            Authentication authentication)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, SQLException
    {

        int resultFromDeleteAction;

        AcmAssociatedTag source = getAssociatedTagService().getAssociatedTagByTagIdAndObjectIdAndType(tagId, objectId, objectType);
        resultFromDeleteAction = getAssociatedTagService().removeAssociatedTag(source);

        if (resultFromDeleteAction == NO_ROW_DELETED)
        {
            if (log.isDebugEnabled())
                log.debug("Associated Tag with tagId:" + tagId + "  on object['" + objectType + "]:[" + objectId + "] not found in the DB");
            getAssociatedTagEventPublisher().publishAssociatedTagDeletedEvent(source, authentication, false);
            return prepareJsonReturnMsg(SUCCESS_MSG, source.getId(), tagId);
        }
        else
        {
            log.debug("Associated Tag with tagId:" + tagId + "  on object['" + objectType + "]:[" + objectId + "] successfully removed");
            getAssociatedTagEventPublisher().publishAssociatedTagDeletedEvent(source, authentication, true);
            return prepareJsonReturnMsg(SUCCESS_MSG, source.getId(), tagId);
        }
    }

    private String prepareJsonReturnMsg(String msg, Long objectId, Long tagId)
    {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedAssociatedTagId", objectId);
        objectToReturnJSON.put("tagId", tagId);
        objectToReturnJSON.put("message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
    }

    public AssociatedTagEventPublisher getAssociatedTagEventPublisher()
    {
        return associatedTagEventPublisher;
    }

    public void setAssociatedTagEventPublisher(AssociatedTagEventPublisher associatedTagEventPublisher)
    {
        this.associatedTagEventPublisher = associatedTagEventPublisher;
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
