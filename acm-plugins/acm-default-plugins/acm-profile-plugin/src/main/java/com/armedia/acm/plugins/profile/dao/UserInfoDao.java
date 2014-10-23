package com.armedia.acm.plugins.profile.dao;

import com.armedia.acm.core.exceptions.AcmObjectNotFoundException;
import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.profile.exception.AcmProfileException;
import com.armedia.acm.plugins.profile.model.UserInfo;
import com.armedia.acm.services.users.model.AcmUser;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

/**
 * Created by marjan.stefanoski on 20.10.2014.
 */
public class UserInfoDao extends AcmAbstractDao<UserInfo> {

    public UserInfo getUserInfoForUser(AcmUser user) throws AcmProfileException, AcmObjectNotFoundException {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<UserInfo> query = builder.createQuery(UserInfo.class);
        Root<UserInfo> d = query.from(UserInfo.class);
        query.select(d).where(builder.equal(d.get("user"), user));
        TypedQuery<UserInfo> dbQuery = getEm().createQuery(query);
        List<UserInfo> results = null;
        results = dbQuery.getResultList();
        if( results.isEmpty()){
            throw new AcmObjectNotFoundException("profile",null, "Object not found",null);
        }
        return results.get(0);
    }

    @Transactional
    public UserInfo updateUserInfo(UserInfo userInfo){
       userInfo = getEm().merge(userInfo);
        return userInfo;
    }

    @Override
    protected Class<UserInfo> getPersistenceClass() {
        return UserInfo.class;
    }
}
