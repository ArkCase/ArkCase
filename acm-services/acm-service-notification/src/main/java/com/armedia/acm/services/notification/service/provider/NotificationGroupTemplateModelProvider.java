package com.armedia.acm.services.notification.service.provider;

import com.armedia.acm.core.AcmObject;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.data.service.AcmDataService;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.provider.model.NotificationGroupModel;
import com.armedia.acm.services.participants.model.AcmAssignedObject;
import com.armedia.acm.services.participants.model.AcmParticipant;

import java.util.Objects;

public class NotificationGroupTemplateModelProvider implements TemplateModelProvider
{
    private AcmDataService dataService;

    @Override
    public Object getModel(Notification notification)
    {
        NotificationGroupModel notificationGroupModelData = new NotificationGroupModel();

        notificationGroupModelData.setObjectNumber(notification.getParentName());
        notificationGroupModelData.setObjectTitle(notification.getParentTitle());

        AcmAbstractDao<AcmObject> dao = getDataService().getDaoByObjectType(notification.getParentType());
        AcmObject acmObject = dao.find(notification.getParentId());

        if(acmObject instanceof AcmAssignedObject)
        {
            AcmAssignedObject acmAssignedObject = (AcmAssignedObject)acmObject;
            AcmParticipant assignee = acmAssignedObject.getParticipants().stream().filter(acmParticipant -> acmParticipant.getParticipantType()
                    .equals("assignee")).findFirst().orElse(null);

            notificationGroupModelData.setAssignee(Objects.nonNull(assignee) ? assignee.getParticipantLdapId() : "");
        }

        return notificationGroupModelData;
    }

    public AcmDataService getDataService()
    {
        return dataService;
    }

    public void setDataService(AcmDataService dataService)
    {
        this.dataService = dataService;
    }
}

