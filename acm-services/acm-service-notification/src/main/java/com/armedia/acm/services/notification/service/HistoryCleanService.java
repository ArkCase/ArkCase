package com.armedia.acm.services.notification.service;

import com.armedia.acm.audit.dao.AuditDao;
import com.armedia.acm.services.notification.dao.NotificationDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by will.phillips on 8/18/2016.
 */
public class HistoryCleanService
{
    private int historyDays = 30;
    private AuditDao auditDao;
    private NotificationDao notificationDao;

    private Logger log = LoggerFactory.getLogger(getClass());

    public void cleanHistory()
    {
        if (historyDays <= 0)
        {
            log.debug("History clearing is disabled. Stopping now.");
            return;
        }

        Date today = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_MONTH, historyDays * -1);
        Date threshold = calendar.getTime();

        log.info("Cleaning out audit events older than " + historyDays + " days...");
        auditDao.purgeAudits(threshold);

        log.info("Cleaning out notifications older than " + historyDays + " days...");
        notificationDao.purgeNotifications(threshold);
    }

    public int getHistoryDays() { return historyDays; }
    public void setHistoryDays(int historyDays) { this.historyDays = historyDays; }
    public AuditDao getAuditDao() { return auditDao; }
    public void setAuditDao(AuditDao auditDao) { this.auditDao = auditDao; }
    public NotificationDao getNotificationDao() { return notificationDao; }
    public void setNotificationDao(NotificationDao notificationDao) { this.notificationDao = notificationDao; }
}
