package com.armedia.acm.services.exemption.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.exemption.model.ExemptionStatute;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class ExemptionStatuteDao extends AcmAbstractDao<ExemptionStatute>
{

    private EntityManager entityManager;

    @Override
    protected Class<ExemptionStatute> getPersistenceClass()
    {
        return ExemptionStatute.class;
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

    public List<ExemptionStatute> getExemptionStatutesByFileIdAndCaseId(Long caseId, Long fileId)
    {
//        String queryText = "SELECT codes.id, codes.exemptionCode, codes.exemptionStatus, codes.exemptionStatute, codes.creator, codes.created, file.fileId, codes.fileVersion, cont.containerObjectId, codes.manuallyFlag "
//                +
//                "FROM AcmContainer cont " +
//                "JOIN EcmFile file ON cont.id = file.container.id " +
//                "JOIN ExemptionCode codes ON file.fileId = codes.fileId " +
//                "WHERE cont.containerObjectId = :caseId " +
//                "AND cont.containerObjectType = 'CASE_FILE' " +
//                "AND file.fileId = :fileId " +
//                "GROUP BY codes.exemptionCode, codes.exemptionStatus";
//
//        TypedQuery<Object[]> query = getEntityManager().createQuery(queryText, Object[].class);
//        query.setParameter("caseId", caseId);
//        query.setParameter("fileId", fileId);
//
//        List<Object[]> exemptionCodeList = query.getResultList();
//
//        List<ExemptionCode> exemptionMappedList = new ArrayList<>();
//        for (Object[] record : exemptionCodeList)
//        {
//            ExemptionCode exemptionCode = new ExemptionCode();
//            exemptionCode.setId((Long) record[0]);
//            exemptionCode.setExemptionCode((String) record[1]);
//            exemptionCode.setExemptionStatus((String) record[2]);
//            exemptionCode.setExemptionStatute((String) record[3]);
//            exemptionCode.setCreator((String) record[4]);
//            exemptionCode.setCreated((Date) record[5]);
//            exemptionCode.setFileId((Long) record[6]);
//            exemptionCode.setFileVersion((String) record[7]);
//            exemptionCode.setParentObjectId((Long) record[8]);
//            exemptionCode.setManuallyFlag((Boolean) record[9]);
//            exemptionMappedList.add(exemptionCode);
//        }
//
        return new ArrayList<>();
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
