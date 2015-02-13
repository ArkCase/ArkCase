package com.armedia.acm.services.notification.dao;


import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

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
    
    public List<Notification> executeQuery(Date lastRunDate, int firstResult, int maxResult, String query)
	{
    	TypedQuery<Object[]> select = getEm().createQuery(query, Object[].class);
		
		select.setParameter("lastRunDate", lastRunDate);
		select.setFirstResult(firstResult);
		select.setMaxResults(maxResult);
		
		List<Notification> retval = new ArrayList<>();
		
		for (Object[] obj : select.getResultList())
		{
			Notification notification = new Notification();
			
			notification.setUser((String) obj[0]);
			notification.setTitle((String) obj[1]);
			notification.setNote((String) obj[2]);
			notification.setType((String) obj[3]);
			notification.setParentId((Long) obj[4]);
			notification.setParentType((String) obj[5]);
			notification.setParentName((String) obj[6]);
			notification.setParentTitle((String) obj[7]);
			notification.setUserEmail((String) obj[8]);
			notification.setStatus(NotificationConstants.STATUS_NEW);
			notification.setAction(NotificationConstants.ACTION_DEFAULT);
			notification.setData("{\"usr\":\"/plugin/" + ((String) obj[5]).toLowerCase() + "/" + ((Long) obj[4]) + "\"}");
			
			retval.add(notification);
		}
		
		return retval;
	}

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

}


