package gov.foia.service;

import com.armedia.acm.services.holiday.service.HolidayConfigurationService;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.users.dao.UserDao;
import com.armedia.acm.services.users.model.AcmUser;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import gov.foia.dao.FOIARequestDao;
import gov.foia.model.FOIARequest;

public class DueDateReminder
{
    private NotificationDao notificationDao;
    private FOIARequestDao foiaRequestDao;
    private HolidayConfigurationService holidayConfigurationService;
    private UserDao userDao;

    public void sendDueDateReminder()
    {

        LocalDate oneDayFromNow = holidayConfigurationService.addWorkingDaysToDate(LocalDate.now(), 1);
        LocalDate fiveDaysFromNow = holidayConfigurationService.addWorkingDaysToDate(LocalDate.now(), 5);

        List<FOIARequest> foiaRequests = foiaRequestDao.findAll();
        for (FOIARequest request : foiaRequests)
        {
            if (request.getStatus() != "RELEASED"
                    && (request.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(oneDayFromNow)
                            || request.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(fiveDaysFromNow)))
            {
                AcmUser user = getUserDao().findByUserId(request.getAssigneeLdapId());

                String dueDateRemainingDays = request.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        .equals(oneDayFromNow) ? "1" : "5";

                Notification notification = new Notification();
                notification.setNote(dueDateRemainingDays);
                notification.setTitle(String.format("Request:%s assigned to %s", request.getCaseNumber(), user.getFullName()));
                notification.setParentId(request.getId());
                notification.setParentType(request.getObjectType());
                notification.setParentName(request.getCaseNumber());
                notification.setParentTitle(request.getTitle());
                notification.setUser(request.getAssigneeLdapId());
                notification.setEmailAddresses(user.getMail());
                notification.setTemplateModelName("requestAssigneeDueDateReminder");
                notification.setAttachFiles(false);
                notificationDao.save(notification);
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
}
