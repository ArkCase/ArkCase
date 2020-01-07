package com.armedia.acm.services.users.dao;

/*-
 * #%L
 * ACM Service: Users
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
import com.armedia.acm.services.users.model.AcmUserAction;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class UserActionDao extends AcmAbstractDao<AcmUserAction>
{

    private Logger LOG = LogManager.getLogger(getClass());

    public List<AcmUserAction> findByUserId(String userId)
    {
        List<AcmUserAction> results = null;

        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<AcmUserAction> query = builder.createQuery(AcmUserAction.class);
        Root<AcmUserAction> userAction = query.from(AcmUserAction.class);

        query.select(userAction);

        query.where(
                builder.and(
                        builder.equal(userAction.<String> get("userId"), userId)));

        TypedQuery<AcmUserAction> dbQuery = getEm().createQuery(query);

        try
        {
            results = dbQuery.getResultList();
        }
        catch (Exception e)
        {
            LOG.info("There is no any User Actions connected with user id " + userId);
        }

        return results;
    }

    public AcmUserAction findByUserIdAndName(String userId, String name)
    {
        AcmUserAction result = null;

        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<AcmUserAction> query = builder.createQuery(AcmUserAction.class);
        Root<AcmUserAction> userAction = query.from(AcmUserAction.class);

        query.select(userAction);

        query.where(
                builder.and(
                        builder.equal(userAction.<String> get("userId"), userId)),
                builder.and(
                        builder.equal(userAction.<String> get("name"), name)));

        TypedQuery<AcmUserAction> dbQuery = getEm().createQuery(query);

        try
        {
            result = dbQuery.getSingleResult();
        }
        catch (Exception e)
        {
            LOG.info("There is no any User Action connected with user id " + userId + " with action name " + name);
        }

        return result;
    }

    @Override
    protected Class<AcmUserAction> getPersistenceClass()
    {
        return AcmUserAction.class;
    }

}
