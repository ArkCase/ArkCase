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
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.AssociatedTagEventPublisher;
import com.armedia.acm.services.tag.service.AssociatedTagService;
import com.armedia.acm.services.tag.service.TagService;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Controller
@RequestMapping({ "/api/v1/service/tag", "/api/latest/service/tag" })
public class AssociateTagAPIController
{

    private final static int ZERO = 0;
    private transient final Logger log = LogManager.getLogger(getClass());
    private AssociatedTagService associatedTagService;
    private TagService tagService;
    private AssociatedTagEventPublisher associatedTagEventPublisher;

    @PreAuthorize("hasPermission(#objectId, #objectType, 'addTag')")
    @RequestMapping(value = "{objectId}/{objectType}/{objectTitle}/{tagId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmAssociatedTag associateTag(@PathVariable("objectId") Long objectId, @PathVariable("objectType") String objectType,
            @PathVariable("objectTitle") String objectTitle, @PathVariable("tagId") Long tagId, Authentication authentication)
            throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, UnsupportedEncodingException
    {

        objectTitle = URLDecoder.decode(objectTitle, "UTF-8");

        log.info("Creating new tag association on object type [{}], title [{}], id [{}], and tagId: {}", objectType, objectTitle, objectId,
                tagId);

        AcmTag tagForAssociating = getTagService().findTag(tagId);
        AcmAssociatedTag newAssociatedTag = null;
        try
        {
            AcmAssociatedTag returnedAssociatedTag = getAssociatedTagService().saveAssociateTag(objectType, objectId, objectTitle,
                    tagForAssociating);
            getAssociatedTagEventPublisher().publishAssociatedTagCreatedEvent(returnedAssociatedTag, authentication, true);
            newAssociatedTag = returnedAssociatedTag;
        }
        catch (Exception e)
        {
            Throwable t = ExceptionUtils.getRootCause(e);
            if (t instanceof SQLIntegrityConstraintViolationException)
            {

                log.debug("Tag associated on object [{}]:[{}] and tagId: {} already exists", objectType, objectId, tagId, e);

                List<AcmAssociatedTag> associatedTagList = getAssociatedTagService().getAcmAssociatedTagByTagIdAndObjectIdAndType(tagId,
                        objectId, objectType);
                if (associatedTagList.isEmpty())
                {

                    log.error("Constraint Violation Exception occurred while trying to assign a tag with tagId: {} on object [{}]:[{}]",
                            objectType, objectId, tagId, e);
                    throw new AcmCreateObjectFailedException(objectType, "Tag Association  on object [" + objectType + "]:[" + objectId
                            + "] and tagId: " + tagId + " was not inserted into the DB", e);
                }
                else
                {
                    newAssociatedTag = associatedTagList.get(ZERO);
                }
            }
            else
            {

                log.error("Exception occurred while trying to associate tag with tagId: {} on object [{}]:[{}]", objectType, objectId,
                        tagId, e);

                getAssociatedTagEventPublisher().publishAssociatedTagCreatedEvent(newAssociatedTag, authentication, false);

                throw new AcmCreateObjectFailedException(objectType,
                        "Tag Association on object [" + objectType + "]:[" + objectId + "] was not inserted into the DB", e);
            }
        }
        return newAssociatedTag;
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

    public TagService getTagService()
    {
        return tagService;
    }

    public void setTagService(TagService tagService)
    {
        this.tagService = tagService;
    }

}
