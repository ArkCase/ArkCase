package com.armedia.acm.services.sequence.service;

import com.armedia.acm.services.sequence.dao.AcmSequenceDao;
import com.armedia.acm.services.sequence.dao.AcmSequenceRegistryDao;
import com.armedia.acm.services.sequence.dao.AcmSequenceResetDao;
import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.model.AcmSequenceEntityId;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceServiceImpl implements AcmSequenceService
{
    private AcmSequenceDao sequenceDao;

    private AcmSequenceRegistryDao sequenceRegistryDao;

    private AcmSequenceResetDao sequenceResetDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceEntity getSequenceEntity(String sequenceName, String sequencePartName)
    {
        return getSequenceDao().getSequenceEntity(sequenceName, sequencePartName);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(maxAttempts = 3, value = OptimisticLockException.class, backoff = @Backoff(delay = 500))
    public AcmSequenceEntity updateSequenceEntity(AcmSequenceEntity sequenceEntity, AcmSequencePart sequencePart, Boolean isReset)
    {
        AcmSequenceEntityId entityId = new AcmSequenceEntityId();
        entityId.setSequenceName(sequenceEntity.getSequenceName());
        entityId.setSequencePartName(sequenceEntity.getSequencePartName());
        AcmSequenceEntity toUpdate = getSequenceDao().find(entityId);

        if (isReset)
        {
            toUpdate.setSequencePartValue(sequencePart.getSequenceStartNumber() + sequencePart.getSequenceIncrementSize());
        }
        else
        {
            toUpdate.setSequencePartValue(toUpdate.getSequencePartValue() + sequencePart.getSequenceIncrementSize());
        }
        return getSequenceDao().save(toUpdate);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<AcmSequenceReset> getSequenceResetList(String sequenceName, String sequencePartName, Boolean resetExecutedFlag)
    {
        return getSequenceResetDao().getSequenceResetList(sequenceName, sequencePartName, resetExecutedFlag);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#updateSequenceReset(com.armedia.acm.services.
     * sequence.model.AcmSequenceReset)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceReset updateSequenceReset(AcmSequenceReset sequenceReset)
    {
        return getSequenceResetDao().save(sequenceReset);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#getSequenceRegistryList(java.lang.String,
     * java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<AcmSequenceRegistry> getSequenceRegistryList(String sequenceName, String sequencePartName,
            Boolean sequencePartValueUsedFlag)
    {
        return getSequenceRegistryDao().getSequenceRegistryList(sequenceName, sequencePartName, sequencePartValueUsedFlag);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.sequence.service.AcmSequenceService#updateSequenceRegistryAsUnused(java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Integer updateSequenceRegistryAsUnused(String sequenceValue)
    {
        return getSequenceRegistryDao().updateSequenceRegistryAsUnused(sequenceValue);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.sequence.service.AcmSequenceService#removeSequenceRegistry(java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Integer removeSequenceRegistry(String sequenceValue)
    {
        return getSequenceRegistryDao().removeSequenceRegistry(sequenceValue);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#removeSequenceRegistry(java.lang.String,
     * java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Integer removeSequenceRegistry(String sequenceName, String sequencePartName)
    {
        return getSequenceRegistryDao().removeSequenceRegistry(sequenceName, sequencePartName);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.sequence.service.AcmSequenceService#saveSequenceEntity(com.armedia.acm.services.sequence
     * .model.AcmSequenceEntity)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceEntity saveSequenceEntity(AcmSequenceEntity sequenceEntity)
    {
        return getSequenceDao().save(sequenceEntity);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.sequence.service.AcmSequenceService#saveSequenceReset(com.armedia.acm.services.sequence.
     * model.AcmSequenceReset)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceReset saveSequenceReset(AcmSequenceReset sequenceReset)
    {
        return getSequenceResetDao().save(sequenceReset);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#saveSequenceRegistry(com.armedia.acm.services.
     * sequence.model.AcmSequenceRegistry)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceRegistry saveSequenceRegistry(AcmSequenceRegistry sequenceRegistry)
    {
        return getSequenceRegistryDao().save(sequenceRegistry);
    }

    /**
     * @return the sequenceDao
     */
    public AcmSequenceDao getSequenceDao()
    {
        return sequenceDao;
    }

    /**
     * @param sequenceDao
     *            the sequenceDao to set
     */
    public void setSequenceDao(AcmSequenceDao sequenceDao)
    {
        this.sequenceDao = sequenceDao;
    }

    /**
     * @return the sequenceRegistryDao
     */
    public AcmSequenceRegistryDao getSequenceRegistryDao()
    {
        return sequenceRegistryDao;
    }

    /**
     * @param sequenceRegistryDao
     *            the sequenceRegistryDao to set
     */
    public void setSequenceRegistryDao(AcmSequenceRegistryDao sequenceRegistryDao)
    {
        this.sequenceRegistryDao = sequenceRegistryDao;
    }

    /**
     * @return the sequenceResetDao
     */
    public AcmSequenceResetDao getSequenceResetDao()
    {
        return sequenceResetDao;
    }

    /**
     * @param sequenceResetDao
     *            the sequenceResetDao to set
     */
    public void setSequenceResetDao(AcmSequenceResetDao sequenceResetDao)
    {
        this.sequenceResetDao = sequenceResetDao;
    }

}
