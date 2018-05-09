package com.armedia.acm.services.transcribe.dao;

import com.armedia.acm.data.AcmAbstractDao;
import com.armedia.acm.services.transcribe.exception.GetTranscribeException;
import com.armedia.acm.services.transcribe.model.Transcribe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;

import java.util.List;

/**
 * Created by Riste Tutureski <riste.tutureski@armedia.com> on 03/05/2018
 */
public class TranscribeDao extends AcmAbstractDao<Transcribe>
{
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    public Transcribe findByMediaVersionId(Long mediaVersionId) throws GetTranscribeException
    {
        String queryString = "SELECT t FROM Transcribe t WHERE t.mediaEcmFileVersion.id=:mediaVersionId";

        TypedQuery<Transcribe> query = getEm().createQuery(queryString, Transcribe.class);
        query.setParameter("mediaVersionId", mediaVersionId);

        String reason = "";

        try
        {
            return query.getSingleResult();
        }
        catch (NoResultException e)
        {
            LOG.warn("There is no Transcribe for MEDIA_VERSION_ID=[{}]. REASON=[{}]", mediaVersionId, e.getMessage());
            return null;
        }
        catch (NonUniqueResultException e)
        {
            reason = String.format(
                    "There is no unique Transcribe found with MEDIA_VERSION_ID=[%d]. More than one Transcribe has the same media version.",
                    mediaVersionId);
            LOG.warn(reason);
        }
        catch (Exception e)
        {
            reason = String.format("Error while retrieving Transcribe with MEDIA_VERSION_ID=[%d]", mediaVersionId);
            LOG.error(reason, e);
        }

        throw new GetTranscribeException(
                String.format("Transcribe for MEDIA_VERSION_ID=[%d] was not retrieved successfully. REASON=[%s]", mediaVersionId, reason));
    }

    public List<Transcribe> findAllByStatus(String status) throws GetTranscribeException
    {
        String queryString = "SELECT t FROM Transcribe t WHERE t.status=:status";

        TypedQuery<Transcribe> query = getEm().createQuery(queryString, Transcribe.class);
        query.setParameter("status", status);

        try
        {
            return query.getResultList();
        }
        catch (Exception e)
        {
            throw new GetTranscribeException(String
                    .format("Transcribe objects with STATUS=[%s] was not retrieved successfully. REASON=[%s]", status, e.getMessage()));
        }
    }

    @Override
    protected Class<Transcribe> getPersistenceClass()
    {
        return Transcribe.class;
    }
}
