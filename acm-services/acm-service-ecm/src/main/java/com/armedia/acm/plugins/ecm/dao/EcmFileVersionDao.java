package com.armedia.acm.plugins.ecm.dao;

/*-
 * #%L
 * ACM Service: Enterprise Content Management
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
import com.armedia.acm.plugins.ecm.model.EcmFileVersion;

import javax.persistence.Query;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/06/2018
 */
public class EcmFileVersionDao extends AcmAbstractDao<EcmFileVersion>
{
    @Override
    protected Class<EcmFileVersion> getPersistenceClass()
    {
        return EcmFileVersion.class;
    }

    public Long getTotalSizeOfFiles(LocalDateTime until)
    {
        String queryText = "SELECT SUM(fileVersion.fileSizeBytes) FROM EcmFileVersion fileVersion WHERE fileVersion.created <= :until";
        Query query = getEm().createQuery(queryText);
        query.setParameter("until", Date.from(ZonedDateTime.of(until, ZoneId.systemDefault()).toInstant()));
        // if no files were added to ArkCase yet, this query may return NULL
        Long totalSize = (Long) query.getSingleResult();
        return totalSize == null ? 0L : totalSize;

    }

    public List<EcmFileVersion> getEcmFileVersionWithSameHash(String fileHash)
    {
        String queryText = "SELECT fileVersion FROM EcmFileVersion fileVersion WHERE fileVersion.fileHash = :fileHash";
        Query query = getEm().createQuery(queryText);
        query.setParameter("fileHash", fileHash);

        List<EcmFileVersion> results = query.getResultList();

        return results;
    }
}
