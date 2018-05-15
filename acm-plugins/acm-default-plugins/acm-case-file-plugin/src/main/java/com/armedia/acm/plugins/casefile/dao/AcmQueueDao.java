package com.armedia.acm.plugins.casefile.dao;

/*-
 * #%L
 * ACM Default Plugin: Case File
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
import com.armedia.acm.plugins.casefile.model.AcmQueue;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

/**
 * Created by nebojsha on 31.08.2015.
 */
public class AcmQueueDao extends AcmAbstractDao<AcmQueue>
{

    @Override
    protected Class<AcmQueue> getPersistenceClass()
    {
        return AcmQueue.class;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AcmQueue findByName(String queueName)
    {
        TypedQuery<AcmQueue> queueQuery = getEm().createQuery(
                "SELECT e FROM " + getPersistenceClass().getSimpleName() + " e WHERE e.name = :name",
                AcmQueue.class);

        queueQuery.setParameter("name", queueName);

        AcmQueue retval = queueQuery.getSingleResult();

        return retval;
    }
}
