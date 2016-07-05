package com.armedia.acm.service.outlook.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.service.outlook.model.OutlookDTO;
import com.armedia.acm.service.outlook.model.OutlookPassword;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

public class OutlookPasswordDao extends AcmAbstractDao<OutlookPassword>
{

    @PersistenceContext
    private EntityManager entityManager;

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    @Transactional
    public void saveOutlookPassword(Authentication authentication, OutlookDTO in)
    {
        // must use a native update, since we don't want to add a password property to an entity class; since
        // if we did that, the password would end up being passed around in POJOs and JSON objects.

        String sql = "UPDATE acm_outlook_password " + "SET cm_outlook_password = ?1 " + "WHERE cm_user_id = ?2 ";

        Query q = getEm().createNativeQuery(sql);
        q.setParameter(1, in.getOutlookPassword());
        q.setParameter(2, authentication.getName());

        int updated = q.executeUpdate();

        if (updated == 0)
        {
            throw new IllegalStateException("No outlook password for user '" + authentication.getName() + "'");
        }

    }

    @Transactional
    public OutlookDTO retrieveOutlookPassword(Authentication authentication)
    {
        // must use a native query, since we don't want to add a password property to an entity class; since
        // if we did that, the password would end up being passed around in POJOs and JSON objects.

        String sql = "SELECT cm_outlook_password " + "FROM acm_outlook_password op " + "WHERE op.cm_user_id = ?1 ";

        Query q = getEm().createNativeQuery(sql);
        q.setParameter(1, authentication.getName());

        try
        {
            Object o = q.getSingleResult();

            log.debug("Query result is of type: " + (o == null ? "null" : o.getClass().getName()));

            String password = (String) o;

            OutlookDTO retval = new OutlookDTO();
            retval.setOutlookPassword(password);

            return retval;
        } catch (PersistenceException pe)
        {
            throw new IllegalStateException("No outlook password for user '" + authentication.getName() + "'");
        }
    }

    public OutlookPassword findByUserId(String userId)
    {
        return getEntityManager().find(OutlookPassword.class, userId);
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }

    @Override
    protected Class<OutlookPassword> getPersistenceClass()
    {
        return OutlookPassword.class;
    }
}
