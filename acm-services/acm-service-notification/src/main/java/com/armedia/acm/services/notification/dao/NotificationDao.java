package com.armedia.acm.services.notification.dao;


import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.notification.model.Notification;
import com.armedia.acm.services.notification.model.NotificationConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Parameter;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NotificationDao extends AcmAbstractDao<Notification>
{
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
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
                        "OR notification.user = :everyone " +
                        "AND notification.status != 'DELETE'"
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
    
    /**
     * This method is used only for notification rules. We have two possibilities - when we want to create notifications ('create' should be true)
     * and get already existing notifications that rich the rules that we want to update
     * 
     * @param parameters
     * @param firstResult
     * @param maxResult
     * @param query
     * @param create
     * @return
     */
    public List<Notification> executeQuery(Map<String, Object> parameters, int firstResult, int maxResult, String query, boolean create)
	{
    	List<Notification> retval = new ArrayList<>();
    	
    	if (create)
    	{
    		retval = createNotifications(parameters, firstResult, maxResult, query);
    	}
    	else
    	{
    		retval = getNotifications(parameters, firstResult, maxResult, query);
    	}
		
		return retval;
	}
    
    /**
     * This method is called when we have mixed query and depends on that query, new notification should be created.
     * 
     * @param parameters
     * @param firstResult
     * @param maxResult
     * @param query
     * @return
     */
    private List<Notification> createNotifications(Map<String, Object> parameters, int firstResult, int maxResult, String query)
    {
    	TypedQuery<Object[]> select = getEm().createQuery(query, Object[].class);
		
    	select = (TypedQuery<Object[]>) populateQueryParameters(select, parameters);
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
    
    /**
     * This method is called when we have to take already existing notifications with rules defined in the query
     * 
     * @param parameters
     * @param firstResult
     * @param maxResult
     * @param query
     * @return
     */
    private List<Notification> getNotifications(Map<String, Object> parameters, int firstResult, int maxResult, String query)
    {
    	Query select = getEm().createQuery(query);
		
    	select = populateQueryParameters(select, parameters);
		select.setFirstResult(firstResult);
		select.setMaxResults(maxResult);
		
		@SuppressWarnings("unchecked")
		List<Notification> retval = (List<Notification>) select.getResultList();
		
		if (null == retval) {
			retval = new ArrayList<>();
        }
		
		return retval;
    }
    
    /**
     * This method will populate query parameters. If JPQL don't have specific property, that will be excluded from adding them in the query
     * 
     * @param query
     * @param parameters
     * @return
     */
    private Query populateQueryParameters(Query query, Map<String, Object> parameters)
    {
    	if (parameters != null)
    	{
    		for (Entry<String, Object> entry : parameters.entrySet())
    		{
    			Parameter<?> parameter = null;
    			try
    			{
    				parameter = query.getParameter(entry.getKey());
    			}
    			catch(Exception e)
    			{
    				LOG.debug("Normal behaviour - the property not exist in the query, so we should not add it.");
    			}
    			
    			if (parameter != null)
    			{
    				query.setParameter(entry.getKey(), entry.getValue());
    			}
    		}
    	}
    	
    	return query;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

}


