package com.armedia.acm.services.exemption.dao;

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
        String queryText = "SELECT statutes.id, statutes.exemptionStatute, statutes.exemptionStatus, statutes.creator, statutes.created, file.fileId, statutes.fileVersion, cont.containerObjectId, statutes.manuallyFlag "
                +
                "FROM AcmContainer cont " +
                "JOIN EcmFile file ON cont.id = file.container.id " +
                "JOIN ExemptionStatute statutes ON file.fileId = statutes.fileId " +
                "WHERE cont.containerObjectId = :caseId " +
                "AND cont.containerObjectType = 'CASE_FILE' " +
                "AND file.fileId = :fileId " +
                "GROUP BY statutes.exemptionStatute";

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
