package gov.foia.listener;

/*-
 * #%L
 * ACM Standard Application: Freedom of Information Act
 * %%
 * Copyright (C) 2014 - 2019 ArkCase LLC
 * %%
 * This file is part of the ArkCase software. 
 * 
 * If the software was purchased under a paid ArkCase license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * ArkCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ArkCase is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with ArkCase. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
        String parentObjectType = dueDateReminderSentEvent.getObjectType();
        Long parentObjectId = dueDateReminderSentEvent.getObjectId();
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
