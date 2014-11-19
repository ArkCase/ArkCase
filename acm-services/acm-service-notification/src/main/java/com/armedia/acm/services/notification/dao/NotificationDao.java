package com.armedia.acm.services.notification.dao;


import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.notification.model.Notification;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class NotificationDao extends AcmAbstractDao<Notification>
{

    @PersistenceContext
    private EntityManager entityManager;



    @Override
    protected Class<Notification> getPersistenceClass()
    {
        return Notification.class;
    }

    /*public Notification findNotificationsById(Long id)
    {
        Query notifications = getEntityManager().createQuery(
                "SELECT notification " + "FROM Notification notification "+
                "WHERE notification.id = :notificationId "
        );

        notifications.setParameter("notificationId", id);
        Notification notificationFound = (Notification) notifications.getSingleResult();

        return notificationFound;
    }*/

    public List<Notification> listNotifications(String user)
    {
        String everyone="EVERYONE";
        Query notification = getEntityManager().createQuery(
                "SELECT notification " +
                        "FROM Notification notification " +
                        "WHERE notification.user = :user " +
                        "OR notification.user = :everyone"
        );
        notification.setParameter("user", user);
        notification.setParameter("everyone", everyone);

        List<Notification> notifications = ( List<Notification> ) notification.getResultList();
        if (null == notifications) {
            notifications = new ArrayList();
        }
        return notifications;
    }

    @Transactional
    public void deleteNotificationById(Long id)
    {
        Query queryToDelete = getEntityManager().createQuery(
                "SELECT notification " +"FROM Notification notification " +
                        "WHERE notification.id = :notificationId"
        );
        queryToDelete.setParameter("notificationId", id);

        Notification notificationToBeDeleted = (Notification) queryToDelete.getSingleResult();
    entityManager.remove(notificationToBeDeleted);
}

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

}


