package com.armedia.acm.services.tag.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.TagEventPublisher;
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

    private TagDao tagDao;
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
            AcmTag returnedTag = getTagDao().getTagByTextOrDescOrName(text, desc, name);

        if(returnedTag !=null) {
            if (log.isDebugEnabled()) {
                log.debug("Tag with id: " + returnedTag.getId() + " name: " + name + " or description: " + desc + " or text:" + text + " already exists");
            }
            throw new AcmCreateObjectFailedException(AcmTag.OBJECT_TYPE, "Tag with Name: " + name + " or Description: " + desc + " or Text: " + text + " already exists in the DB, pls try to delete or update existing one", null);
        }else {
            AcmTag newTag = prepareNewTag(name, desc, text);
            try {
                AcmTag addedTag = getTagDao().save(newTag);
                getTagEventPublisher().publishTagCreatedEvent(addedTag, authentication, true);
                return addedTag;
            } catch (Exception e) {
                if (log.isErrorEnabled())
                    log.error("Exception occurred while trying to insert new Tag into DB with name: " + name + " and Description: " + desc + " and Text: " + text, e);
                getTagEventPublisher().publishTagCreatedEvent(newTag, authentication, false);
                throw new AcmCreateObjectFailedException(AcmTag.OBJECT_TYPE, "Tag with Name: " + name + " or Description: " + desc + " or Text: " + text + " was not inserted into DB due to exception", e);
            }
        }
    }

    private AcmTag prepareNewTag(String name,String desc, String value) {
        AcmTag newTag = new AcmTag();
        newTag.setTagText(value);
        newTag.setTagName(name);
        newTag.setTagDescription(desc);
        return newTag;
    }

    public TagEventPublisher getTagEventPublisher() {
        return tagEventPublisher;
    }

    public void setTagEventPublisher(TagEventPublisher tagEventPublisher) {
        this.tagEventPublisher = tagEventPublisher;
    }

    public TagDao getTagDao() {
        return tagDao;
    }

    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }
}
