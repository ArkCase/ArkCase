package com.armedia.acm.services.tag.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.TagEventPublisher;
import com.armedia.acm.services.tag.service.TagService;
import org.activiti.engine.impl.util.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@RequestMapping({"/api/v1/service/tag", "/api/latest/service/tag"})
public class RemoveExistingTagAPIController {

    private TagService tagService;
    private TagEventPublisher tagEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());


    private final static String SUCCESS_MSG = "Tag removed successfully: ";
    private final static String USER_ACTION = "DELETE";

    @RequestMapping(value = "/{tagId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String removeTag(
            @PathVariable("tagId") Long tagId,
            Authentication authentication,
            HttpSession httpSession
    ) throws AcmUserActionFailedException {
        if (log.isInfoEnabled()) {
            log.info("Removing tag with tagId: " + tagId);
        }
        try {
            AcmTag tag = tagService.findTag(tagId);
            if (tag != null) {
                getTagService().removeTag(tag);
                if (log.isDebugEnabled())
                    log.debug("Tag with tagId:" + tagId + "  successfully removed");
                getTagEventPublisher().publishTagDeletedEvent(tag, authentication, true);
                return prepareJsonReturnMsg(SUCCESS_MSG, tagId);
            } else {
                if (log.isDebugEnabled())
                    log.debug("Tag with tagId:" + tagId + " not found in the DB");
                getTagEventPublisher().publishTagDeletedEvent(tag, authentication, false);
                return prepareJsonReturnMsg(SUCCESS_MSG, tagId);
            }
        } catch ( SQLException e ) {
            if (log.isErrorEnabled())
            log.error("SQL Exception was thrown while deleting tag with tagId: "+ tagId);
            throw new AcmUserActionFailedException(USER_ACTION,AcmTag.OBJECT_TYPE,tagId,"SQL Exception was thrown while deleting tag",e);
        }
    }


    private String prepareJsonReturnMsg(String msg, Long tagId) {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedTagId", tagId);
        objectToReturnJSON.put("Message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
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

