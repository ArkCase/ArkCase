/**
 * 
 */
package com.armedia.acm.service.history.dao;

/*-
 * #%L
 * ACM Service: History Service
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
import com.armedia.acm.service.history.model.AcmHistory;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;

/**
 * @author riste.tutureski
 *
 */
public class AcmHistoryDao extends AcmAbstractDao<AcmHistory>
{

    @Override
    protected Class<AcmHistory> getPersistenceClass()
    {
        return AcmHistory.class;
    }

    @Transactional
    public List<AcmHistory> findByPersonId(Long personId)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<AcmHistory> query = builder.createQuery(AcmHistory.class);
        Root<AcmHistory> history = query.from(AcmHistory.class);

        query.select(history);

        query.where(
                builder.and(
                        builder.equal(history.<Long> get("personId"), personId)));

        TypedQuery<AcmHistory> dbQuery = getEm().createQuery(query);
        List<AcmHistory> results = dbQuery.getResultList();

        return results;
    }

    @Transactional
    public int deleteByPersonIdAndObjectType(Long personId, String objectType)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();

        CriteriaDelete<AcmHistory> delete = builder.createCriteriaDelete(AcmHistory.class);
        Root<AcmHistory> acmHistory = delete.from(AcmHistory.class);

        delete.where(
                builder.and(
                        builder.equal(acmHistory.<Long> get("personId"), personId)),
                builder.and(
                        builder.equal(acmHistory.<String> get("objectType"), objectType)));

        Query query = getEm().createQuery(delete);

        return query.executeUpdate();
    }

}
