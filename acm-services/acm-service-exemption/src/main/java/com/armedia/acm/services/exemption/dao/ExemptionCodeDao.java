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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.transaction.annotation.Transactional;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.exemption.model.ExemptionCode;

/**
 * Created by ana.serafimoska
 */

public class ExemptionCodeDao extends AcmAbstractDao<ExemptionCode>
{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    protected Class<ExemptionCode> getPersistenceClass()
    {
        return ExemptionCode.class;
    }

    @Transactional
    public void deleteExemptionCode(Long id)
    {
        ExemptionCode exemptionCode = getEm().find(ExemptionCode.class, id);
        if (exemptionCode != null)
        {
            getEm().remove(exemptionCode);
        }
    }

    @Transactional
    public List<ExemptionCode> getExemptionCodesByFileIdAndVersionTag(Long fileId, String fileVersion, List<String> exemptionCodes)
    {
        String queryText = "SELECT exemptionCode " +
                "FROM ExemptionCode exemptionCode " +
                "WHERE exemptionCode.fileId = :fileId " +
                "AND exemptionCode.fileVersion =:fileVersion " +
                "AND exemptionCode.manuallyFlag <> TRUE";

        TypedQuery<ExemptionCode> query = getEntityManager().createQuery(queryText, ExemptionCode.class);
        query.setParameter("fileId", fileId);
        query.setParameter("fileVersion", fileVersion);

        List<ExemptionCode> exemptionCodeList = query.getResultList();
        if (exemptionCodeList == null)
        {
            exemptionCodeList = new ArrayList<>();
        }
        return exemptionCodeList;

    }

    @Transactional
    public void deleteNotAssociatedExemptionCodeWithGivenFileId(List<String> removeList, Long fileId)
    {
        if (removeList.size() > 0)
        {
            for (String exemptionCode : removeList)
            {
                String gueryText = "DELETE FROM ExemptionCode exemptionCode " +
                        "WHERE exemptionCode.fileId = :fileId " +
                        "AND exemptionCode.exemptionCode = :exemptionCode ";

                TypedQuery<ExemptionCode> removeQuery = getEntityManager().createQuery(gueryText, ExemptionCode.class);
                removeQuery.setParameter("fileId", fileId);
                removeQuery.setParameter("exemptionCode", exemptionCode);
                removeQuery.executeUpdate();
            }
        }
    }

    @Transactional
    public Integer updateExemptionCodesStatusAfterBurn(Long realFileId, String status, String user)
    {
        String queryText = "UPDATE ExemptionCode exemptionCode " +
                "SET exemptionCode.exemptionStatus = :status " +
                "WHERE exemptionCode.fileId = :realFileId " +
                "AND exemptionCode.manuallyFlag <> TRUE ";

        Query query = getEm().createQuery(queryText);
        query.setParameter("status", status);
        query.setParameter("realFileId", realFileId);

        return query.executeUpdate();
    }

    @Transactional
    public List<ExemptionCode> getExemptionCodesByFileIdAndCaseId(Long caseId, Long fileId)
    {
        String queryText = "SELECT codes.id, codes.exemptionCode, codes.exemptionStatus, codes.exemptionStatute, codes.creator, codes.created, file.fileId, codes.fileVersion, cont.containerObjectId, codes.manuallyFlag "
                +
                "FROM AcmContainer cont " +
                "JOIN EcmFile file ON cont.id = file.container.id " +
                "JOIN ExemptionCode codes ON file.fileId = codes.fileId " +
                "WHERE cont.containerObjectId = :caseId " +
                "AND cont.containerObjectType = 'CASE_FILE' " +
                "AND file.fileId = :fileId " +
                "GROUP BY codes.exemptionCode, codes.exemptionStatus";

        TypedQuery<Object[]> query = getEntityManager().createQuery(queryText, Object[].class);
        query.setParameter("caseId", caseId);
        query.setParameter("fileId", fileId);

        List<Object[]> exemptionCodeList = query.getResultList();

        List<ExemptionCode> exemptionMappedList = new ArrayList<>();
        for (Object[] record : exemptionCodeList)
        {
            ExemptionCode exemptionCode = new ExemptionCode();
            exemptionCode.setId((Long) record[0]);
            exemptionCode.setExemptionCode((String) record[1]);
            exemptionCode.setExemptionStatus((String) record[2]);
            exemptionCode.setExemptionStatute((String) record[3]);
            exemptionCode.setCreator((String) record[4]);
            exemptionCode.setCreated((Date) record[5]);
            exemptionCode.setFileId((Long) record[6]);
            exemptionCode.setFileVersion((String) record[7]);
            exemptionCode.setParentObjectId((Long) record[8]);
            exemptionCode.setManuallyFlag((Boolean) record[9]);
            exemptionMappedList.add(exemptionCode);
        }

        return exemptionMappedList;
    }

    public EntityManager getEntityManager()
    {
        return entityManager;
    }

}
