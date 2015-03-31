package com.armedia.acm.services.tag.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.TagEventPublisher;
import com.armedia.acm.services.tag.service.TagService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/tag", "/api/latest/service/tag"})
public class AddNewTagAPIController {

    private TagService tagService;
    private TagEventPublisher tagEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTag addNewTag(
            @RequestParam(value="name", required = true) String name,
            @RequestParam(value="desc", required = true) String desc,
            @RequestParam(value="text", required = true) String text,
            Authentication authentication) throws AcmCreateObjectFailedException {

        if ( log.isInfoEnabled() ) {
            log.info("Creating new tag with text:"+text+" description: " + desc + "and name: "+name );
        }
            AcmTag returnedTag = getTagService().getTagByTextOrDescOrName(text, desc, name);

        if(returnedTag !=null) {
            if (log.isDebugEnabled()) {
                log.debug("Tag with id: " + returnedTag.getId() + " name: " + name + " or description: " + desc + " or text:" + text + " already exists");
            }
            throw new AcmCreateObjectFailedException(AcmTag.OBJECT_TYPE, "Tag with Name: " + name + " or Description: " + desc + " or Text: " + text + " already exists in the DB, pls try to delete or update existing one", null);
        } else {
            AcmTag addedTag = null;
            try {
                addedTag = getTagService().saveTag(name, desc, text);
                getTagEventPublisher().publishTagCreatedEvent(addedTag, authentication, true);
                return addedTag;
            } catch (Exception e) {
                if (log.isErrorEnabled())
                    log.error("Exception occurred while trying to insert new Tag into DB with name: " + name + " and Description: " + desc + " and Text: " + text, e);
                getTagEventPublisher().publishTagCreatedEvent(addedTag, authentication, false);
                throw new AcmCreateObjectFailedException(AcmTag.OBJECT_TYPE, "Tag with Name: " + name + " or Description: " + desc + " or Text: " + text + " was not inserted into DB due to exception", e);
            }
        }
    }

    public TagEventPublisher getTagEventPublisher() {
        return tagEventPublisher;
    }

    public void setTagEventPublisher(TagEventPublisher tagEventPublisher) {
        this.tagEventPublisher = tagEventPublisher;
    }

    public TagService getTagService() {
        return tagService;
    }

    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

}
