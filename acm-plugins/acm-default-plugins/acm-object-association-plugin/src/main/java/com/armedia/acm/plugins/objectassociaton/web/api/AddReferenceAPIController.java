package com.armedia.acm.plugins.objectassociaton.web.api;

import com.armedia.acm.core.exceptions.AcmUserActionFailedException;
import com.armedia.acm.plugins.objectassociation.model.Reference;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationEventPublisher;
import com.armedia.acm.plugins.objectassociation.service.ObjectAssociationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 
 * @author vladimir.radeski
 *
 */

@Controller
@RequestMapping({ "/api/v1/service/objectassociation", "/api/latest/service/objectassociation" })
public class AddReferenceAPIController
{
    private Logger log = LoggerFactory.getLogger(getClass());

    private ObjectAssociationService objectAssociationService;
    private ObjectAssociationEventPublisher eventPublisher;

    @RequestMapping(method = RequestMethod.POST, value = "/reference", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    private Reference addReference(@RequestBody Reference ref, Authentication auth) throws AcmUserActionFailedException
    {
        try
        {
            Long referenceId = ref.getReferenceId();
            String referenceNumber = ref.getReferenceNumber();
            String referenceType = ref.getReferenceType();
            String referenceTitle = ref.getReferenceTitle();
            String referenceStatus = ref.getReferenceStatus();
            Long parentId = ref.getParentId();
            String parentType = ref.getParentType();
            objectAssociationService.addReference(referenceId, referenceNumber, referenceType, referenceTitle, referenceStatus, parentId, parentType);
            getEventPublisher().publishAddReferenceEvent(ref, auth, true);
            return ref;

        } catch (Exception e)
        {
            log.error("Could not add reference: {} ", e.getMessage(), e);
            getEventPublisher().publishAddReferenceEvent(ref, auth, false);

            throw new AcmUserActionFailedException("reference", e.getMessage(), null, null, e);
        }

    }

    public ObjectAssociationService getObjectAssociationService()
    {
        return objectAssociationService;
    }

    public void setObjectAssociationService(ObjectAssociationService objectAssociationService)
    {
        this.objectAssociationService = objectAssociationService;
    }

    public ObjectAssociationEventPublisher getEventPublisher()
    {
        return eventPublisher;
    }

    public void setEventPublisher(ObjectAssociationEventPublisher eventPublisher)
    {
        this.eventPublisher = eventPublisher;
    }

}
