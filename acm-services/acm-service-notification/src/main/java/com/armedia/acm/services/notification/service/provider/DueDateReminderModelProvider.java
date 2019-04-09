package com.armedia.acm.services.notification.service.provider;

import com.armedia.acm.services.notification.event.DueDateReminderSentEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.service.provider.model.GenericTemplateModel;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

public class DueDateReminderModelProvider implements TemplateModelProvider, ApplicationEventPublisherAware
{
    private ApplicationEventPublisher applicationEventPublisher;

    @Override
    public Object getModel(Notification notification)
    {
        GenericTemplateModel genericTemplateModel = new GenericTemplateModel();

        genericTemplateModel.setObjectNumber(notification.getParentName());
        genericTemplateModel.setObjectTitle(notification.getParentTitle());
        genericTemplateModel.setOtherObjectValue(notification.getNote());

        generateDueDateReminderSentEvent(notification);

        return genericTemplateModel;
    }

    private void generateDueDateReminderSentEvent(Notification notification)
    {
        DueDateReminderSentEvent dueDateReminderSentEvent =
                new DueDateReminderSentEvent(notification, notification.getParentType(), notification.getParentId(), Long.valueOf(notification.getNote()));
        applicationEventPublisher.publishEvent(dueDateReminderSentEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
