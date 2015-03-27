package com.armedia.acm.services.tag.web.api;

import com.armedia.acm.core.exceptions.AcmCreateObjectFailedException;
import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.services.tag.dao.AssociatedTagDao;
import com.armedia.acm.services.tag.dao.TagDao;
import com.armedia.acm.services.tag.model.AcmAssociatedTag;
import com.armedia.acm.services.tag.model.AcmTag;
import com.armedia.acm.services.tag.service.AssociatedTagEventPublisher;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

/**
 * Created by marjan.stefanoski on 24.03.2015.
 */
@Controller
@RequestMapping({"/api/v1/service/tag", "/api/latest/service/tag"})
public class AssociateTagAPIController {

    private AssociatedTagDao associatedTagDao;
    private TagDao tagDao;
    private AssociatedTagEventPublisher associatedTagEventPublisher;

    private final static int ZERO = 0;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @RequestMapping(value = "/{userId}/{objectId}/{objectType}/{tagId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public AcmAssociatedTag associateTag(
            @PathVariable("userId") String userId,
            @PathVariable("objectId") Long objectId,
            @PathVariable("objectType") String objectType,
            @PathVariable("tagId") Long tagId,
            Authentication authentication) throws AcmUserActionFailedException, AcmCreateObjectFailedException, AcmObjectNotFoundException {

        if ( log.isInfoEnabled() ) {
            log.info("Creating new tag association for user:"+userId+" on object['" + objectType + "]:[" + objectId + "] and tagId: "+tagId );
        }
        //TODO check if exists objectId with objcetType if not throw exception

           AcmTag tagForAssociating = getTagDao().find(tagId);
           AcmAssociatedTag newAssociatedTag = prepareAcmAssociateTag(objectType,objectId,tagForAssociating);
           try{
               AcmAssociatedTag returnedAssociatedTag  = getAssociatedTagDao().save(newAssociatedTag);
               getAssociatedTagEventPublisher().publishAssociatedTagCreatedEvent(returnedAssociatedTag,authentication,true);
               newAssociatedTag = returnedAssociatedTag;
           } catch ( Exception e ) {
               Throwable t =  ExceptionUtils.getRootCause(e);
               if ( t instanceof SQLIntegrityConstraintViolationException) {
                   if (log.isDebugEnabled())
                       log.debug("Tag associated on object['" + objectType + "]:[" + objectId + "] by user: " + userId + " already exists", e);

                   List<AcmAssociatedTag> associatedTagList = getAssociatedTagDao().getAcmAssociatedTagByTagIdAndObjectIdAndType(tagId, objectId, objectType);
                   if(associatedTagList.isEmpty()){
                       if(log.isErrorEnabled())
                           log.error("Constraint Violation Exception occurred while trying to assign a tag with tagId: "+tagId+ "  on object[" + objectType + "]:[" + objectId + "] for user: " + userId,e);
                       throw new AcmCreateObjectFailedException(objectType,"Tag Association for user: "+userId+" on object [" + objectType + "]:[" + objectId + "] and tagId: " +tagId+" was not inserted into the DB",e);
                   } else {
                       newAssociatedTag = associatedTagList.get(ZERO);
                   }
               } else {
                   if(log.isErrorEnabled())
                       log.error("Exception occurred while trying to associate tag with tagId: "+tagId+ " on object[" + objectType + "]:[" + objectId + "] for user: " + userId,e);

                   getAssociatedTagEventPublisher().publishAssociatedTagCreatedEvent(newAssociatedTag,authentication,false);

                   throw new AcmCreateObjectFailedException(objectType,"Subscription for user: "+userId+" on object [" + objectType + "]:[" + objectId + "] was not inserted into the DB",e);
               }
           }
           return newAssociatedTag;
    }

    private AcmAssociatedTag prepareAcmAssociateTag(String objectType, Long objectId, AcmTag tag) {
        AcmAssociatedTag acmAssociatedTag = new AcmAssociatedTag();
        acmAssociatedTag.setParentType(objectType);
        acmAssociatedTag.setParentId(objectId);
        acmAssociatedTag.setTag(tag);
        return acmAssociatedTag;
    }

    public AssociatedTagEventPublisher getAssociatedTagEventPublisher() {
        return associatedTagEventPublisher;
    }

    public void setAssociatedTagEventPublisher(AssociatedTagEventPublisher associatedTagEventPublisher) {
        this.associatedTagEventPublisher = associatedTagEventPublisher;
    }

    public TagDao getTagDao() {
        return tagDao;
    }

    public void setTagDao(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    public AssociatedTagDao getAssociatedTagDao() {
        return associatedTagDao;
    }

    public void setAssociatedTagDao(AssociatedTagDao associatedTagDao) {
        this.associatedTagDao = associatedTagDao;
    }
}
