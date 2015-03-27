package com.armedia.acm.services.tag.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marjan.stefanoski on 27.03.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/tag", "/api/latest/service/tag"})
public class ListAssociatedTagsByObjectTypeAndIdAPIController {

    private AssociatedTagDao associatedTagDao;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value="/{objectId}/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmAssociatedTag> listAssociatedTaqsBy(
            @PathVariable("objectId") Long objectId,
            @PathVariable("objectType") String objectType,
            Authentication auth) throws AcmUserActionFailedException, AcmObjectNotFoundException {
        if ( log.isInfoEnabled() ) {
            log.info("Listing assigned tags for objectId: "+objectId+" and object type: "+objectType);
        }
        List<AcmAssociatedTag> acmAssociatedTags = null;
        try {
            acmAssociatedTags = getAssociatedTagDao().getAcmAssociatedTagsByObjectIdAndType(objectId,objectType);
        } catch (AcmObjectNotFoundException e) {
            if (log.isDebugEnabled())
                log.debug("No Associated Tags are Found for objectId: "+ objectId+" and objectType: "+objectType, e);
            return new ArrayList<>();
        }
        return acmAssociatedTags;
    }

    public AssociatedTagDao getAssociatedTagDao() {
        return associatedTagDao;
    }

    public void setAssociatedTagDao(AssociatedTagDao associatedTagDao) {
        this.associatedTagDao = associatedTagDao;
    }
}
