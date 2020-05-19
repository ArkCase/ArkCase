/**
 * 
 */
package com.armedia.acm.plugins.consultation.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.plugins.consultation.model.ChangeConsultationStatus;
import com.armedia.acm.services.participants.model.AcmParticipant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class ChangeConsultationStatusDao extends AcmAbstractDao<ChangeConsultationStatus>
{
    private Logger LOG = LogManager.getLogger(getClass());

    @Override
    protected Class<ChangeConsultationStatus> getPersistenceClass()
    {
        return ChangeConsultationStatus.class;
    }

    @Transactional
    public int delete(List<AcmParticipant> participants)
    {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();

        CriteriaDelete<AcmParticipant> delete = builder.createCriteriaDelete(AcmParticipant.class);
        Root<AcmParticipant> acmParticipant = delete.from(AcmParticipant.class);

        List<Long> ids = new ArrayList<>();
        if (null != participants && participants.size() > 0)
        {

            for (AcmParticipant participant : participants)
            {
                ids.add(participant.getId());
            }

        }

        delete.where(
                acmParticipant.<Long> get("id").in(ids));

        Query query = getEm().createQuery(delete);

        return query.executeUpdate();
    }

    public ChangeConsultationStatus findByConsultationId(Long consultationId)
    {
        ChangeConsultationStatus result = null;

        CriteriaBuilder builder = getEm().getCriteriaBuilder();

        CriteriaQuery<ChangeConsultationStatus> query = builder.createQuery(ChangeConsultationStatus.class);
        Root<ChangeConsultationStatus> changeConsultationStatus = query.from(ChangeConsultationStatus.class);

        query.select(changeConsultationStatus);

        query.where(
                builder.and(
                        builder.equal(changeConsultationStatus.<Long> get("consultationId"), consultationId)),
                builder.and(
                        builder.equal(changeConsultationStatus.<String> get("status"), "ACTIVE")));

        TypedQuery<ChangeConsultationStatus> dbQuery = getEm().createQuery(query);

        try
        {
            result = dbQuery.getSingleResult();
        }
        catch (Exception e)
        {
            LOG.info("There is no any results.");
        }

        return result;
    }
}
