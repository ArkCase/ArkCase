package com.armedia.acm.services.notification;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-notification.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-notification-plugin-test.xml",
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-particpants.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class NotificationIT
{
    @Autowired
    private NotificationDao notificationDao;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AuditPropertyEntityAdapter auditPropertyEntityAdapter;


    @Test
    @Transactional
    public void saveNotification() throws Exception
    {
        auditPropertyEntityAdapter.setUserId("notifyUser");

        Notification n = new Notification();

        n.setStatus("testStatus");
        n.setNote("testNote");
        n.setAction("Acknow");
        n.setType("type");
        n.setData("data");
        n.setUser("user");


        Notification saved = notificationDao.save(n);

        assertNotNull(saved.getId());

        notificationDao.deleteNotificationById(saved.getId());


        log.info("Notification ID: " + saved.getId());
    }


}
