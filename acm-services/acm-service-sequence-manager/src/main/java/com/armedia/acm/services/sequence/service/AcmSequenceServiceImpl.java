package com.armedia.acm.services.sequence.service;

/*-
 * #%L
 * ACM Service: Sequence Manager
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

import com.armedia.acm.services.sequence.dao.AcmSequenceDao;
import com.armedia.acm.services.sequence.dao.AcmSequenceRegistryDao;
import com.armedia.acm.services.sequence.dao.AcmSequenceResetDao;
import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.model.AcmSequenceEntityId;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;
import com.armedia.acm.services.sequence.model.AcmSequenceResetId;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmSequenceServiceImpl implements AcmSequenceService
{
    private final Logger log = LogManager.getLogger(getClass());

    private AcmSequenceDao sequenceDao;

    private AcmSequenceRegistryDao sequenceRegistryDao;

    private AcmSequenceResetDao sequenceResetDao;

    @Override
    public AcmSequenceEntity getSequenceEntity(String sequenceName, String sequencePartName, FlushModeType flushModeType)
            throws AcmSequenceException
    {
        log.info("Getting Sequence Entity for [{}] [{}]", sequenceName, sequencePartName);
        try
        {
            return getSequenceDao().getSequenceEntity(sequenceName, sequencePartName, flushModeType);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to get Sequence Entity for [%s] [%s]", sequenceName, sequencePartName), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceEntity updateSequenceEntity(AcmSequenceEntity sequenceEntity, AcmSequencePart sequencePart, Boolean isReset)
            throws AcmSequenceException
    {
        try
        {
            AcmSequenceEntityId entityId = new AcmSequenceEntityId();
            entityId.setSequenceName(sequenceEntity.getSequenceName());
            entityId.setSequencePartName(sequenceEntity.getSequencePartName());
            AcmSequenceEntity toUpdate = getSequenceDao().find(entityId);

            if (isReset)
            {
                toUpdate.setSequencePartValue(
                        Long.valueOf(sequencePart.getSequenceStartNumber() + sequencePart.getSequenceIncrementSize()));
            }
            else
            {
                toUpdate.setSequencePartValue(toUpdate.getSequencePartValue() + sequencePart.getSequenceIncrementSize());
            }
            return getSequenceDao().save(toUpdate);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to update Sequence Entity for [%s] [%s]", sequenceEntity.getSequenceName(),
                            sequenceEntity.getSequencePartName()),
                    e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#getSequenceResetList(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<AcmSequenceReset> getSequenceResetList(String sequenceName, String sequencePartName) throws AcmSequenceException
    {
        return getSequenceResetList(sequenceName, sequencePartName, null, null);
    }

    @Override
    public List<AcmSequenceReset> getSequenceResetList(String sequenceName, String sequencePartName, Boolean resetExecutedFlag,
            FlushModeType flushModeType)
            throws AcmSequenceException
    {
        log.info("Getting Sequence Reset List for [{}] [{}]", sequenceName, sequencePartName);
        try
        {
            return getSequenceResetDao().getSequenceResetList(sequenceName, sequencePartName, resetExecutedFlag, flushModeType);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to get Sequence Reset List for [%s] [%s]", sequenceName, sequencePartName), e);
        }

    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#updateSequenceReset(com.armedia.acm.services.
     * sequence.model.AcmSequenceReset)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceReset updateSequenceReset(AcmSequenceReset sequenceReset) throws AcmSequenceException
    {
        log.info("Updating Sequence Reset for [{}] [{}]", sequenceReset.getSequenceName(), sequenceReset.getSequencePartName());
        try
        {
            return getSequenceResetDao().save(sequenceReset);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(String.format("Unable to update Sequence Reset for [%s] [%s]", sequenceReset.getSequenceName(),
                    sequenceReset.getSequencePartName()), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#getSequenceRegistryList(java.lang.String,
     * java.lang.String)
     */
    @Override
    public List<AcmSequenceRegistry> getSequenceRegistryList(String sequenceName, String sequencePartName,
            Boolean sequencePartValueUsedFlag, FlushModeType flushModeType) throws AcmSequenceException
    {
        log.info("Getting Sequence Registry List for [{}] [{}]", sequenceName, sequencePartName);
        try
        {
            return getSequenceRegistryDao().getSequenceRegistryList(sequenceName, sequencePartName, sequencePartValueUsedFlag,
                    flushModeType);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to get Sequence Registry List for [%s] [%s]", sequenceName, sequencePartName), e);
        }
    }

    @Override
    public List<AcmSequenceRegistry> getSequenceRegistryList() throws AcmSequenceException
    {
        log.info("Getting Sequence Registry List");
        try
        {
            return getSequenceRegistryDao().getSequenceRegistryList();
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(String.format("Unable to get Sequence Registry List"), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceEntity updateSequenceEntity(AcmSequenceEntity acmSequenceEntity) throws AcmSequenceException
    {
        log.info("Update Sequence Number");
        try
        {
            return getSequenceDao().save(acmSequenceEntity);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(String.format("Unable to update Sequence Number"), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.sequence.service.AcmSequenceService#updateSequenceRegistryAsUnused(java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Integer updateSequenceRegistryAsUnused(String sequenceValue) throws AcmSequenceException
    {
        log.info("Updating Sequence Registry [{}]", sequenceValue);
        try
        {
            return getSequenceRegistryDao().updateSequenceRegistryAsUnused(sequenceValue);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to update Sequence Registry for [%s]", sequenceValue), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.sequence.service.AcmSequenceService#removeSequenceRegistry(java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Integer removeSequenceRegistry(String sequenceValue) throws AcmSequenceException
    {
        log.info("Removing Sequence Registry for [{}]", sequenceValue);
        try
        {
            return getSequenceRegistryDao().removeSequenceRegistry(sequenceValue);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to remove Sequence Registry for [%s]", sequenceValue), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#removeSequenceRegistry(java.lang.String,
     * java.lang.String)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Integer removeSequenceRegistry(String sequenceName, String sequencePartName) throws AcmSequenceException
    {
        log.info("Removing Sequence Registry for [{}] [{}]", sequenceName, sequencePartName);
        try
        {
            return getSequenceRegistryDao().removeSequenceRegistry(sequenceName, sequencePartName);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to remove Sequence Registry for [%s] [%s]", sequenceName, sequencePartName), e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#deleteSequenceReset(com.armedia.acm.services.
     * sequence.model.AcmSequenceReset)
     */
    @Override
    @Transactional
    public void deleteSequenceReset(AcmSequenceReset sequenceReset) throws AcmSequenceException
    {
        log.info("Removing Sequence Reset for [{}] [{}]", sequenceReset.getSequenceName(), sequenceReset.getSequencePartName());
        try
        {
            AcmSequenceResetId resetId = new AcmSequenceResetId();
            resetId.setSequenceName(sequenceReset.getSequenceName());
            resetId.setSequencePartName(sequenceReset.getSequencePartName());
            resetId.setResetDate(sequenceReset.getResetDate());
            AcmSequenceReset toRemove = getSequenceResetDao().find(resetId);
            getSequenceResetDao().remove(toRemove);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to remove Sequence Reset for [%s] [%s]", sequenceReset.getSequenceName(),
                            sequenceReset.getSequencePartName()),
                    e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.sequence.service.AcmSequenceService#saveSequenceEntity(com.armedia.acm.services.sequence
     * .model.AcmSequenceEntity)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceEntity saveSequenceEntity(AcmSequenceEntity sequenceEntity) throws AcmSequenceException
    {
        log.info("Saving Sequence Entity for [{}] [{}]", sequenceEntity.getSequenceName(), sequenceEntity.getSequencePartName());
        try
        {
            return getSequenceDao().save(sequenceEntity);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to save Sequence Entity [%s] [%s]", sequenceEntity.getSequenceName(),
                            sequenceEntity.getSequencePartName()),
                    e);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * com.armedia.acm.services.sequence.service.AcmSequenceService#saveSequenceReset(com.armedia.acm.services.sequence.
     * model.AcmSequenceReset)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceReset saveSequenceReset(AcmSequenceReset sequenceReset) throws AcmSequenceException
    {
        log.info("Saving Sequence Reset for [{}] [{}] [{}]", sequenceReset.getSequenceName(), sequenceReset.getSequencePartName(),
                sequenceReset.getResetDate());
        try
        {
            return getSequenceResetDao().save(sequenceReset);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to save Sequence Registry [%s] [%s] [%s]", sequenceReset.getSequenceName(),
                            sequenceReset.getSequencePartName(), sequenceReset.getResetDate()),
                    e);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#saveSequenceRegistry(com.armedia.acm.services.
     * sequence.model.AcmSequenceRegistry)
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceRegistry saveSequenceRegistry(AcmSequenceRegistry sequenceRegistry) throws AcmSequenceException
    {
        log.info("Saving Sequence Registry for [{}] [{}] [{}]", sequenceRegistry.getSequenceValue(), sequenceRegistry.getSequenceName(),
                sequenceRegistry.getSequencePartName());
        try
        {
            return getSequenceRegistryDao().save(sequenceRegistry);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to save Sequence Registry [%s] [%s] [%s]", sequenceRegistry.getSequenceValue(),
                            sequenceRegistry.getSequenceName(), sequenceRegistry.getSequencePartName()),
                    e);
        }
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
