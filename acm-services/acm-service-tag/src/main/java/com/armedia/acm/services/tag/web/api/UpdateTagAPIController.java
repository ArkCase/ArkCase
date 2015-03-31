package com.armedia.acm.services.tag.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.model.AcmTagDto;
import com.armedia.acm.services.tag.service.TagEventPublisher;
import com.armedia.acm.services.tag.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */

@Controller
@RequestMapping({"/api/v1/service/tag", "/api/latest/service/tag"})
public class UpdateTagAPIController {


    private TagService tagService;
    private TagEventPublisher tagEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping( method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTag updateNewTag(
            @RequestBody AcmTagDto in,
            Authentication authentication) throws AcmUserActionFailedException {

        if (log.isInfoEnabled()) {
            log.info("Update tag: " +in.getTagId()+"  with text:" + in.getTagText() + " description: " + in.getTagDescription() + "and name: " + in.getTagName());
        }

        AcmTag tagForUpdate = getTagService().findTag(in.getTagId());
        AcmTag updatedTag;
        try {
            updatedTag = getTagService().updateTag(in.getTagId(), in.getTagName(), in.getTagText(), in.getTagDescription());
            getTagEventPublisher().publishTagUpdatedEvent(updatedTag,authentication,true);
        } catch ( SQLException e ) {
            if( log.isErrorEnabled() )
                log.error("Exception occurred while updating tag with a tagId: "+in.getTagId(),e);
            getTagEventPublisher().publishTagUpdatedEvent(tagForUpdate,authentication,false);
            throw new AcmUserActionFailedException("Update a Tag",AcmTag.OBJECT_TYPE,in.getTagId(),"Updating tag failed!",e);
        }
        return updatedTag;
    }

    public TagService getTagService() {
        return tagService;
    }

    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    public TagEventPublisher getTagEventPublisher() {
        return tagEventPublisher;
    }

    public void setTagEventPublisher(TagEventPublisher tagEventPublisher) {
        this.tagEventPublisher = tagEventPublisher;
    }
}
