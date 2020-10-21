package com.armedia.acm.services.exemption.dao;

/*-
 * #%L
 * ACM Service: Exemption
 * %%
 * Copyright (C) 2014 - 2020 ArkCase LLC
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
import com.armedia.acm.services.exemption.model.ExemptionStatute;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ExemptionStatuteDao extends AcmAbstractDao<ExemptionStatute>
{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<ExemptionStatute> getPersistenceClass()
    {
        return ExemptionStatute.class;
    }

    public List<ExemptionStatute> getExemptionStatutesByFileIdAndCaseId(Long caseId, Long fileId)
    {
        String queryText = "SELECT statute.id, statute.exemptionStatute, statute.exemptionStatus, statute.creator, statute.created, file.fileId, statute.fileVersion, cont.containerObjectId, statute.manuallyFlag "
                +
                "FROM AcmContainer cont " +
                "JOIN EcmFile file ON cont.id = file.container.id " +
                "JOIN ExemptionStatute statute ON file.fileId = statute.fileId " +
                "WHERE cont.containerObjectId = :caseId " +
                "AND cont.containerObjectType = 'CASE_FILE' " +
                "AND file.fileId = :fileId " +
                "GROUP BY statute.exemptionStatute";

        TypedQuery<Object[]> query = getEntityManager().createQuery(queryText, Object[].class);
        query.setParameter("caseId", caseId);
        query.setParameter("fileId", fileId);

        List<Object[]> exemptionStatuteList = query.getResultList();

        List<ExemptionStatute> exemptionMappedList = new ArrayList<>();
        for (Object[] record : exemptionStatuteList)
        {
            ExemptionStatute exemptionStatute = new ExemptionStatute();
            exemptionStatute.setId((Long) record[0]);
            exemptionStatute.setExemptionStatute((String) record[1]);
            exemptionStatute.setExemptionStatus((String) record[2]);
            exemptionStatute.setCreator((String) record[3]);
            exemptionStatute.setCreated((Date) record[4]);
            exemptionStatute.setFileId((Long) record[5]);
            exemptionStatute.setFileVersion((String) record[6]);
            exemptionStatute.setParentObjectId((Long) record[7]);
            exemptionStatute.setManuallyFlag((Boolean) record[8]);
            exemptionMappedList.add(exemptionStatute);
        }

        return exemptionMappedList;
    }

    @Transactional
    public void deleteExemptionStatute(Long id)
    {
        ExemptionStatute exemptionStatute = getEm().find(ExemptionStatute.class, id);
        if (exemptionStatute != null)
        {
            getEm().remove(exemptionStatute);
        }
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager)
    {
        this.entityManager = entityManager;
    }
}
