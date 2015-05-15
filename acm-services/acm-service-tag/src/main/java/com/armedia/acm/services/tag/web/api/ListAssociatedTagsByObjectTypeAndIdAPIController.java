package com.armedia.acm.services.tag.web.api;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.AssociatedTagService;
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
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by marjan.stefanoski on 27.03.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/tag", "/api/latest/service/tag"})
public class ListAssociatedTagsByObjectTypeAndIdAPIController {

    private AssociatedTagService associatedTagService;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value="/{objectId}/{objectType}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<AcmTag> listAssociatedTaqsBy(
            @PathVariable("objectId") Long objectId,
            @PathVariable("objectType") String objectType,
            Authentication auth) throws AcmUserActionFailedException, AcmObjectNotFoundException {
        if ( log.isInfoEnabled() ) {
            log.info("Listing assigned tags for objectId: "+objectId+" and object type: "+objectType);
        }
        List<AcmAssociatedTag> acmAssociatedTags;
        List<AcmTag> acmTags;
        try {
            acmAssociatedTags = getAssociatedTagService().getAcmAssociatedTagsByObjectIdAndType(objectId,objectType,auth);
            acmTags = retrieveTagList(acmAssociatedTags);
        } catch (AcmObjectNotFoundException e) {
            if (log.isDebugEnabled())
                log.debug("No Associated Tags are Found for objectId: "+ objectId+" and objectType: "+objectType, e);
            return new ArrayList<>();
        }
        return acmTags;
    }

    private List<AcmTag> retrieveTagList(List<AcmAssociatedTag> acmAssociatedTags){
        return  acmAssociatedTags.stream().map(s -> s.getTag()).collect(Collectors.toList());
    }

    public AssociatedTagService getAssociatedTagService() {
        return associatedTagService;
    }

    public void setAssociatedTagService(AssociatedTagService associatedTagService) {
        this.associatedTagService = associatedTagService;
    }
}
