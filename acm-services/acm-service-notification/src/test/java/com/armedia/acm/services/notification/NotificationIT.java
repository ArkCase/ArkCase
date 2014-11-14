package com.armedia.acm.services.notification;

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

import java.util.Date;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-notification.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-mule-context-manager.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-mule-context-manager.xml",
        "/spring/spring-library-activiti-actions.xml",
        "/spring/spring-library-activemq.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-drools-monitor.xml",
        "/spring/spring-library-cmis-configuration.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-event.xml"})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class NotificationIT
{
    @Autowired
    private NotificationDao notificationDao;

    private Logger log = LoggerFactory.getLogger(getClass());


    @Test
    @Transactional
    public void saveNotification() throws Exception
    {
        Notification n = new Notification();
        //n.setModifier("testModifier");
        n.setCreator("testCreator");
        n.setCreated(new Date());
        //n.setModified(new Date());
        n.setStatus("testStatus");
        n.setNote("testNote");
        n.setAction("Acknow");
        n.setComment("TestComment");

        Notification saved = notificationDao.save(n);

        assertNotNull(saved.getId());

        //notificationDao.deleteNotificationById(saved.getId());
        

        log.info("Notification ID: " + saved.getId());        
    }
 
    
}
