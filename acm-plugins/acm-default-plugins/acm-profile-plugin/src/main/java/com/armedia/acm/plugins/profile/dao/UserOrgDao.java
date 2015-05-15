package com.armedia.acm.plugins.profile.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.profile.model.OutlookDTO;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.plugins.profile.model.UserOrgConstants;
import com.armedia.acm.services.users.model.AcmUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class UserOrgDao extends AcmAbstractDao<UserOrg> {

    private transient final Logger log = LoggerFactory.getLogger(getClass());

    public UserOrg getUserOrgForUserId(String userId) throws AcmObjectNotFoundException
    {
        String jpql = "SELECT uo FROM UserOrg uo where uo.user.userId = :userId";

        TypedQuery<UserOrg> query = getEm().createQuery(jpql, UserOrg.class);

        query.setParameter("userId", userId);

        try
        {
            UserOrg retval = query.getSingleResult();
            return retval;
        }
        catch ( NoResultException e)
        {
            throw new AcmObjectNotFoundException(UserOrgConstants.OBJECT_TYPE, null, "No user profile for user " +
                    "'" + userId + "'", e);
        }
    }

    public UserOrg getUserOrgForUser(AcmUser user) throws AcmObjectNotFoundException {

        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<UserOrg> query = builder.createQuery(UserOrg.class);
        Root<UserOrg> d = query.from(UserOrg.class);
        query.select(d).where(builder.equal(d.get("user"), user));
        TypedQuery<UserOrg> dbQuery = getEm().createQuery(query);
        List<UserOrg> results = null;
        results = dbQuery.getResultList();
        if( results.isEmpty()){
            throw new AcmObjectNotFoundException("profile",null, "Object not found",null);
        }
        return results.get(0);
    }

    @Transactional
    public void saveOutlookPassword(Authentication authentication, OutlookDTO in)
    {
        // must use a native update, since we don't want to add a password property to an entity class; since
        // if we did that, the password would end up being passed around in POJOs and JSON objects.

        String sql = "UPDATE acm_user_org uo " +
                "SET uo.cm_ms_outlook_password = ?1 " +
                "WHERE uo.cm_user = ?2 ";

        Query q = getEm().createNativeQuery(sql);
        q.setParameter(1, in.getOutlookPassword());
        q.setParameter(2, authentication.getName());

        int updated = q.executeUpdate();

        if ( updated == 0 )
        {
            throw new IllegalStateException("No profile for user '" + authentication.getName() + "'");
        }

    }

    @Transactional
    public OutlookDTO retrieveOutlookPassword(Authentication authentication)
    {
        // must use a native query, since we don't want to add a password property to an entity class; since
        // if we did that, the password would end up being passed around in POJOs and JSON objects.

        String sql = "SELECT cm_ms_outlook_password " +
                "FROM acm_user_org uo " +
                "WHERE uo.cm_user = ?1 ";

        Query q = getEm().createNativeQuery(sql);
        q.setParameter(1, authentication.getName());

        try
        {
            Object o = q.getSingleResult();

            log.debug("Query result is of type: " + o.getClass().getName());

            String password = (String) o;

            OutlookDTO retval = new OutlookDTO();
            retval.setOutlookPassword(password);

            return retval;
        }
        catch (PersistenceException pe)
        {
            throw new IllegalStateException("No profile for user '" + authentication.getName() + "'");
        }
    }

    @Override
    protected Class<UserOrg> getPersistenceClass() {
        return UserOrg.class;
    }


}


