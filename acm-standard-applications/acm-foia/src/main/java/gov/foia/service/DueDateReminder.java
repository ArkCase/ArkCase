package gov.foia.service;

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

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.holiday.service.HolidayConfigurationService;
import com.armedia.acm.services.labels.service.TranslationService;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;
import com.armedia.acm.services.templateconfiguration.model.Template;
import com.armedia.acm.services.templateconfiguration.service.CorrespondenceTemplateManager;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.Date;
import java.util.List;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

public class DueDateReminder
{
    private NotificationDao notificationDao;
    private FOIARequestDao foiaRequestDao;
    private HolidayConfigurationService holidayConfigurationService;
    private UserDao userDao;
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;
    private TranslationService translationService;
    private CorrespondenceTemplateManager templateManager;

    public void sendDueDateReminder()
    {
        auditPropertyEntityAdapter.setUserId(NotificationConstants.SYSTEM_USER);

        Date oneDayFromNow = holidayConfigurationService.addWorkingDaysToDate(new Date(), 1);
        Date fiveDaysFromNow = holidayConfigurationService.addWorkingDaysToDate(new Date(), 5);

        List<FOIARequest> foiaRequests = foiaRequestDao.findAllNotReleasedRequests();
        for (FOIARequest request : foiaRequests)
        {
            if (!request.getQueue().toString().equals("Hold") && request.getDueDate() != null)
            {
                int daysDiffOneDay = Days.daysBetween(new DateTime(oneDayFromNow.getTime()), new DateTime(request.getDueDate().getTime()))
                        .getDays();
                int daysDiffFiveDays = Days
                        .daysBetween(new DateTime(fiveDaysFromNow.getTime()), new DateTime(request.getDueDate().getTime())).getDays();

                if (daysDiffOneDay == 0 || daysDiffFiveDays == 0)
                {
                    AcmUser user = request.getAssigneeLdapId() != null ? userDao.findByUserId(request.getAssigneeLdapId()) : null;

                    String dueDateRemainingDays = daysDiffOneDay == 0 ? "1" : "5";
                    String emailSubject = "";
                    Template template = templateManager.findTemplate("requestAssigneeDueDateReminder.html");
                    if (template != null)
                    {
                        emailSubject = template.getEmailSubject();
                    }
                    Notification notification = new Notification();
                    notification.setNote(dueDateRemainingDays);
                    notification.setTitle(String.format(translationService.translate(NotificationConstants.REQUEST_ASSIGNED),
                            request.getCaseNumber(), user != null ? user.getFullName() : ""));
                    notification.setParentId(request.getId());
                    notification.setParentType(request.getObjectType());
                    notification.setParentName(request.getCaseNumber());
                    notification.setParentTitle(request.getTitle());
                    notification.setUser(request.getAssigneeLdapId());
                    notification.setEmailAddresses(user != null ? user.getMail() : "");
                    notification.setTemplateModelName("requestAssigneeDueDateReminder");
                    notification.setAttachFiles(false);
                    notification.setSubject(emailSubject);
                    notificationDao.save(notification);
                }
            }
        }
    }

    public NotificationDao getNotificationDao()
    {
        return notificationDao;
    }

    public void setNotificationDao(NotificationDao notificationDao)
    {
        this.notificationDao = notificationDao;
    }

    public FOIARequestDao getFoiaRequestDao()
    {
        return foiaRequestDao;
    }

    public void setFoiaRequestDao(FOIARequestDao foiaRequestDao)
    {
        this.foiaRequestDao = foiaRequestDao;
    }

    public HolidayConfigurationService getHolidayConfigurationService()
    {
        return holidayConfigurationService;
    }

    public void setHolidayConfigurationService(HolidayConfigurationService holidayConfigurationService)
    {
        this.holidayConfigurationService = holidayConfigurationService;
    }

    public UserDao getUserDao()
    {
        return userDao;
    }

    public void setUserDao(UserDao userDao)
    {
        this.userDao = userDao;
    }

    public AuditPropertyEntityAdapter getAuditPropertyEntityAdapter()
    {
        return auditPropertyEntityAdapter;
    }

    public void setAuditPropertyEntityAdapter(AuditPropertyEntityAdapter auditPropertyEntityAdapter)
    {
        this.auditPropertyEntityAdapter = auditPropertyEntityAdapter;
    }

    public TranslationService getTranslationService()
    {
        return translationService;
    }

    public void setTranslationService(TranslationService translationService)
    {
        this.translationService = translationService;
    }

    public CorrespondenceTemplateManager getTemplateManager()
    {
        return templateManager;
    }

    public void setTemplateManager(CorrespondenceTemplateManager templateManager)
    {
        this.templateManager = templateManager;
    }
}
