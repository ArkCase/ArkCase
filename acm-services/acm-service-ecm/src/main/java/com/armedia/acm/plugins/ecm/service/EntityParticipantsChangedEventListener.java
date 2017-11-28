package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.core.exceptions.AcmAccessControlException;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
import com.armedia.acm.plugins.ecm.model.EcmFileConstants;
import com.armedia.acm.plugins.ecm.service.impl.EcmFileParticipantService;
import com.armedia.acm.services.dataaccess.model.AcmEntityParticipantsChangedEvent;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.List;

public class EntityParticipantsChangedEventListener implements ApplicationListener<AcmEntityParticipantsChangedEvent>
{

    private final transient Logger log = LoggerFactory.getLogger(getClass());

    private EcmFileParticipantService fileParticipantService;

    @Override
    public void onApplicationEvent(AcmEntityParticipantsChangedEvent event)
    {
        AcmObject obj = (AcmObject) event.getSource();
        List<AcmParticipant> originalParticipants = event.getOriginalParticipants();

        if (obj instanceof AcmAssignedObject && obj instanceof AcmContainerEntity &&
                !((AcmAssignedObject) obj).getObjectType().equals(EcmFileConstants.OBJECT_FILE_TYPE) &&
                !((AcmAssignedObject) obj).getObjectType().equals(EcmFileConstants.OBJECT_FOLDER_TYPE))
        {
            // inherit participants
            if (obj.getId() == null)
            {
                ((AcmAssignedObject) obj).getParticipants().forEach(participant -> participant.setReplaceChildrenParticipant(true));
            }
            try
            {
                getFileParticipantService().inheritParticipantsFromAssignedObject(
                        ((AcmAssignedObject) obj).getParticipants(),
                        originalParticipants,
                        ((AcmContainerEntity) obj).getContainer());
            }
            catch (AcmAccessControlException e)
            {
                log.error(
                        String.format("Failed to inherit file participants for entity type={%s} and id={%s}", obj.getObjectType(),
                                obj.getId()),
                        e);
            }

            // inherit restricted flag
            getFileParticipantService().setRestrictedFlagRecursively(((AcmAssignedObject) obj).getRestricted(),
                    ((AcmContainerEntity) obj).getContainer());
        }
    }

    public EcmFileParticipantService getFileParticipantService()
    {
        return fileParticipantService;
    }

    public void setFileParticipantService(EcmFileParticipantService fileParticipantService)
    {
        this.fileParticipantService = fileParticipantService;
    }
}
