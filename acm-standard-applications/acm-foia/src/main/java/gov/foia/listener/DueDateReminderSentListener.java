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
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.event.DueDateReminderSentEvent;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;
import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;
import org.springframework.context.ApplicationListener;

public class DueDateReminderSentListener implements ApplicationListener<DueDateReminderSentEvent>
{
    private FOIARequestDao foiaRequestDao;
    private UserDao userDao;
    private NotificationDao notificationDao;

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
                
                if(foiaRequest.getStatus().toUpperCase() != "RELEASED") 
                {
                    AcmUser user = getUserDao().findByUserId(foiaRequest.getAssigneeLdapId());
                    String assigneeFullName = user.getFullName();

                    Notification notification = new Notification();
                    if (dueDateRemainingDays.equals(1L)) {
                        notification.setNote("1");
                    } else if (dueDateRemainingDays.equals(5L)) {
                        notification.setNote("5");
                    }
                    notification.setTitle(String.format("Request:%s assigned to %s", foiaRequest.getCaseNumber(), assigneeFullName));
                    notification.setType("user");
                    notification.setParentId(foiaRequest.getId());
                    notification.setParentType(foiaRequest.getObjectType());
                    notification.setParentName(foiaRequest.getCaseNumber());
                    notification.setParentTitle(foiaRequest.getTitle());
                    notification.setUser(foiaRequest.getAssigneeLdapId());
                    notification.setEmailAddresses(user.getMail());
                    notification.setTemplateModelName("requestAssigneeDueDateReminder");
                    notification.setData("{\"usr\":\"/plugin/" + (foiaRequest.getObjectType().toLowerCase() + "/" + foiaRequest.getId() + "\"}"));
                    notification.setAttachFiles(false);
                    notificationDao.save(notification);
                }
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

    public UserDao getUserDao() 
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao) 
    {
        this.userDao = userDao;
    }

    public NotificationDao getNotificationDao() 
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao) 
    {
        this.notificationDao = notificationDao;
    }
}
