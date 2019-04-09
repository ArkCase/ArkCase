package gov.foia.listener;

import com.armedia.acm.plugins.casefile.model.CaseFile;
import com.armedia.acm.services.notification.event.DueDateReminderSentEvent;
import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;
import org.springframework.context.ApplicationListener;

public class DueDateReminderSentListener implements ApplicationListener<DueDateReminderSentEvent>
{
    private FOIARequestDao foiaRequestDao;

    @Override
    public void onApplicationEvent(DueDateReminderSentEvent dueDateReminderSentEvent)
    {
        String parentObjectType = dueDateReminderSentEvent.getParentObjectType();
        Long parentObjectId = dueDateReminderSentEvent.getParentObjectId();
        Long dueDateRemainingDays = (Long) dueDateReminderSentEvent.getEventProperties().getOrDefault("dueDateRemainingDays", 0);

        if("CASE_FILE".equals(parentObjectType) && dueDateRemainingDays > 0)
        {
            CaseFile caseFile = getFoiaRequestDao().find(parentObjectId);
            if(caseFile instanceof FOIARequest)
            {
                FOIARequest foiaRequest = (FOIARequest) caseFile;
                if(dueDateRemainingDays.equals(1L))
                {
                    foiaRequest.setOneDayReminderSent(true);
                }
                else if(dueDateRemainingDays.equals(5L))
                {
                    foiaRequest.setFiveDaysReminderSent(true);
                }

                getFoiaRequestDao().save(foiaRequest);
            }
        }
    }

    public FOIARequestDao getFoiaRequestDao()
    {
        return foiaRequestDao;
    }

    public void setFoiaRequestDao(FOIARequestDao foiaRequestDao)
    {
        this.foiaRequestDao = foiaRequestDao;
    }
}
