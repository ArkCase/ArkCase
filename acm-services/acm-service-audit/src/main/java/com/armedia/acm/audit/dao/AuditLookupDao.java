package com.armedia.acm.audit.dao;

/*-
 * #%L
 * ACM Service: Audit Library
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

import com.armedia.acm.audit.model.AcmAuditLookup;
import com.armedia.acm.data.AcmAbstractDao;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

import java.sql.SQLException;
import java.util.List;

public class AuditLookupDao extends AcmAbstractDao<AcmAuditLookup>
{

    @Override
    protected Class<AcmAuditLookup> getPersistenceClass()
    {
        return AcmAuditLookup.class;
    }

    @Transactional
    public void deleteAllAuditsFormLookupTabel() throws SQLException
    {

        TypedQuery<AcmAuditLookup> selectQuery = getEm().createQuery("SELECT a FROM AcmAuditLookup a ", AcmAuditLookup.class);
        List<AcmAuditLookup> results;

        results = selectQuery.getResultList();
        if (!results.isEmpty())
        {
            for (AcmAuditLookup aul : results)
            {
                getEm().remove(aul);
            }
        }
    }
}
