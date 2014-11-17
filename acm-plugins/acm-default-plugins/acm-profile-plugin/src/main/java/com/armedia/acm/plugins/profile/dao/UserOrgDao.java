package com.armedia.acm.plugins.profile.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.person.model.Organization;
import com.armedia.acm.plugins.person.model.Person;
import com.armedia.acm.plugins.profile.exception.AcmProfileException;
import com.armedia.acm.plugins.profile.model.UserOrg;
import com.armedia.acm.services.users.model.AcmUser;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @PersistenceContext
    private EntityManager entityManager;

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
    public void deleteUserOrgById(Long userOrgId){
        UserOrg userOrgToBeDeleted = getEm().find(UserOrg.class,userOrgId);
        getEm().remove(userOrgToBeDeleted);
    }

    public UserOrg getUserByOrganization(Organization org) throws AcmObjectNotFoundException {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<UserOrg> query = builder.createQuery(UserOrg.class);
        Root<UserOrg> d = query.from(UserOrg.class);
        query.select(d).where(builder.equal(d.get("organization"), org));
        TypedQuery<UserOrg> dbQuery = getEm().createQuery(query);
        List<UserOrg> results = null;
        results = dbQuery.getResultList();
        if( results.isEmpty()){
            throw new AcmObjectNotFoundException("profile",null, "Object not found",null);
        }
        return results.get(0);
    }

    @Transactional
    public UserOrg updateUserInfo(UserOrg userOrgInfo){
        userOrgInfo = getEm().merge(userOrgInfo);
        return userOrgInfo;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    protected Class<UserOrg> getPersistenceClass() {
        return UserOrg.class;
    }
}
