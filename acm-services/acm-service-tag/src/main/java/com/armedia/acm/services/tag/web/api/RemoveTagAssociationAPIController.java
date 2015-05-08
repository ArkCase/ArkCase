package com.armedia.acm.services.tag.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.service.AssociatedTagEventPublisher;
import com.armedia.acm.services.tag.service.AssociatedTagService;
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

import java.sql.SQLException;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/tag", "/api/latest/service/tag"})
public class RemoveTagAssociationAPIController {

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    private final static String SUCCESS_MSG = "Associated Tag Removed Successfully";

    private final static int NO_ROW_DELETED = 0;


    private AssociatedTagService associatedTagService;
    private AssociatedTagEventPublisher associatedTagEventPublisher;

    @RequestMapping(value = "/{objectId}/{objectType}/{tagId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteTag(
            @PathVariable("objectId") Long objectId,
            @PathVariable("objectType") String objectType,
            @PathVariable("tagId") Long tagId,
            Authentication authentication ) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException, SQLException {

        int resultFromDeleteAction;

        AcmAssociatedTag source = getAssociatedTagService().getAssociatedTagByTagIdAndObjectIdAndType(tagId,objectId,objectType);
        resultFromDeleteAction = getAssociatedTagService().removeAssociatedTag(source);

        if ( resultFromDeleteAction == NO_ROW_DELETED ) {
            if( log.isDebugEnabled() )
                log.debug("Associated Tag with tagId:"+tagId+"  on object['" + objectType + "]:[" + objectId + "] not found in the DB");
            getAssociatedTagEventPublisher().publishAssociatedTagDeletedEvent(source, authentication, false);
            return prepareJsonReturnMsg( SUCCESS_MSG, source.getId(), tagId );
        } else {
            log.debug("Associated Tag with tagId:"+tagId+"  on object['" + objectType + "]:[" + objectId + "] successfully removed");
            getAssociatedTagEventPublisher().publishAssociatedTagDeletedEvent(source,authentication, true);
            return prepareJsonReturnMsg(SUCCESS_MSG, source.getId(), tagId);
        }
    }

    private String prepareJsonReturnMsg( String msg,Long objectId, Long tagId ) {
        JSONObject objectToReturnJSON = new JSONObject();
        objectToReturnJSON.put("deletedAssociatedTagId", objectId);
        objectToReturnJSON.put("tagId", tagId);
        objectToReturnJSON.put("message", msg);
        String objectToReturn;
        objectToReturn = objectToReturnJSON.toString();
        return objectToReturn;
    }

    public AssociatedTagEventPublisher getAssociatedTagEventPublisher() {
        return associatedTagEventPublisher;
    }

    public void setAssociatedTagEventPublisher(AssociatedTagEventPublisher associatedTagEventPublisher) {
        this.associatedTagEventPublisher = associatedTagEventPublisher;
    }

    public AssociatedTagService getAssociatedTagService() {
        return associatedTagService;
    }

    public void setAssociatedTagService(AssociatedTagService associatedTagService) {
        this.associatedTagService = associatedTagService;
    }
}
