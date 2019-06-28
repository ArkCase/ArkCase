package com.armedia.acm.services.notification;

/*-
 * #%L
 * ACM Service: Notification
 * %%
 * Copyright (C) 2014 - 2018 ArkCase LLC
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

import static org.junit.Assert.assertNotNull;

import com.armedia.acm.data.AuditPropertyEntityAdapter;
import com.armedia.acm.services.notification.dao.NotificationDao;
import com.armedia.acm.services.notification.model.Notification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/spring/spring-library-acm-encryption.xml",
        "/spring/spring-library-authentication-token.xml",
        "/spring/spring-library-context-holder.xml",
        "/spring/spring-library-object-converter.xml",
        "/spring/spring-library-notification-plugin-IT.xml",
        "/spring/spring-library-ecm-file-lock.xml",
        "/spring/spring-library-core-api.xml",
        "/spring/spring-library-data-access-control.xml",
        "/spring/spring-library-data-source.xml",
        "/spring/spring-library-ecm-file.xml",
        "/spring/spring-library-ecm-tika.xml",
        "/spring/spring-library-ms-outlook-integration.xml",
        "/spring/spring-library-notification.xml",
        "/spring/spring-library-particpants.xml",
        "/spring/spring-library-property-file-manager.xml",
        "/spring/spring-library-search.xml",
        "/spring/spring-library-service-data.xml",
        "/spring/spring-library-user-service.xml",
        "/spring/spring-library-activiti-configuration.xml",
        "/spring/spring-library-drools-rule-monitor.xml",
        "/spring/spring-library-object-lock.xml",
        "/spring/spring-library-email.xml",
        "/spring/spring-library-email-smtp.xml",
        "/spring/spring-library-calendar-config-service.xml",
        "/spring/spring-library-configuration.xml",
        "/spring/spring-library-convert-folder-service.xml",
        "/spring/spring-library-folder-watcher.xml",
        "/spring/spring-library-calendar-integration-exchange-service.xml",
        "/spring/spring-library-hazelcast-config.xml",
        "/spring/spring-library-holiday.xml",
        "/spring/spring-test-quartz-scheduler.xml"
})
@TransactionConfiguration(defaultRollback = true, transactionManager = "transactionManager")
public class NotificationIT
{
    static
    {
        String userHomePath = System.getProperty("user.home");
        System.setProperty("acm.configurationserver.propertyfile", userHomePath + "/.arkcase/acm/conf.yml");
    }

    @Autowired
    private NotificationDao notificationDao;
    private Logger log = LogManager.getLogger(getClass());

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
