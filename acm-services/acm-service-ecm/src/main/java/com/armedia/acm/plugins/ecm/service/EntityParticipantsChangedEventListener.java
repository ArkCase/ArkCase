package com.armedia.acm.plugins.ecm.service;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.plugins.ecm.model.AcmContainerEntity;
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

        if (obj instanceof AcmAssignedObject && obj instanceof AcmContainerEntity)
        {
            // inherit participants
            if (obj.getId() == null)
            {
                ((AcmAssignedObject) obj).getParticipants().forEach(participant -> participant.setReplaceChildrenParticipant(true));
            }

            log.debug("Inheriting file participants from " + obj.getObjectType() + "[" + obj.getId() + "]");
            getFileParticipantService().inheritParticipantsFromAssignedObject(
                    ((AcmAssignedObject) obj).getParticipants(),
                    originalParticipants,
                    ((AcmContainerEntity) obj).getContainer(), ((AcmAssignedObject) obj).getRestricted());
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
