package com.armedia.acm.services.notification.dao;

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

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationDao extends AcmAbstractDao<Notification>
{
    private final Logger LOG = LogManager.getLogger(getClass());

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<Notification> getPersistenceClass()
    {
        return Notification.class;
    }

    @Override
    public String getSupportedObjectType()
    {
        return NotificationConstants.OBJECT_TYPE;
    }

    @Transactional
    public int purgeNotifications(Date threshold)
    {
        int retval = 0;

        Query update = getEm().createQuery("DELETE FROM " +
                "Notification notification " +
                "WHERE " +
                "notification.modified <= :threshold");

        update.setParameter("threshold", threshold);

        try
        {
            retval = update.executeUpdate();
        }
        catch (Exception e)
        {
            LOG.error("Cannot purge notifications.", e);
        }

        return retval;
    }

    public List<Notification> listNotifications(String user)
    {
        String everyone = "EVERYONE";
        Query notification = getEntityManager().createQuery(
                "SELECT notification " +
                        "FROM Notification notification " +
                        "WHERE notification.user = :user " +
                        "OR notification.user = :everyone " +
                        "AND notification.status != 'DELETE'");
        notification.setParameter("user", user);
        notification.setParameter("everyone", everyone);

        List<Notification> notifications = notification.getResultList();
        if (null == notifications)
        {
            notifications = new ArrayList<>();
        }
        return notifications;
    }

    @Transactional
    public void deleteNotificationById(Long id)
    {
        Query queryToDelete = getEntityManager().createQuery(
                "SELECT notification " + "FROM Notification notification " +
                        "WHERE notification.id = :notificationId");
        queryToDelete.setParameter("notificationId", id);

        Notification notificationToBeDeleted = (Notification) queryToDelete.getSingleResult();
        entityManager.remove(notificationToBeDeleted);
    }

    public List<Notification> getNotificationsToProcess()
    {
        String queryText = "SELECT n FROM Notification n WHERE n.state is null";
        TypedQuery<Notification> query = entityManager.createQuery(queryText, Notification.class);
        return query.getResultList();
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }
}
