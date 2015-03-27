package com.armedia.acm.services.tag.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.TagEventPublisher;
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

    private TagDao tagDao;
    private TagEventPublisher tagEventPublisher;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{tagId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmTag updateNewTag(
            @PathVariable("tagId") Long tagId,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "desc", required = true) String desc,
            @RequestParam(value = "text", required = true) String text,
            Authentication authentication) throws AcmCreateObjectFailedException {

        if (log.isInfoEnabled()) {
            log.info("Update tag: " +tagId+"  with text:" + text + " description: " + desc + "and name: " + name);
        }

        AcmTag tagForUpdate = getTagDao().find(tagId);
        tagForUpdate.setTagName(name);
        tagForUpdate.setTagText(text);
        tagForUpdate.setTagDescription(desc);

        AcmTag updatedTag = null;

        try {
            updatedTag = getTagDao().updateTag(tagForUpdate);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updatedTag;
    }

    public TagDao getTagDao() {
        return tagDao;
    }

    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    public TagEventPublisher getTagEventPublisher() {
        return tagEventPublisher;
    }

    public void setTagEventPublisher(TagEventPublisher tagEventPublisher) {
        this.tagEventPublisher = tagEventPublisher;
    }
}
