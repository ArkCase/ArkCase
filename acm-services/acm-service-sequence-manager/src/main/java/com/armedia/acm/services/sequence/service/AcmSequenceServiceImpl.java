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

import com.armedia.acm.core.exceptions.AcmAppErrorJsonMsg;
import com.armedia.acm.services.sequence.annotation.AcmSequence;
import com.armedia.acm.services.sequence.dao.AcmSequenceDao;
import com.armedia.acm.services.sequence.dao.AcmSequenceRegistryDao;
import com.armedia.acm.services.sequence.dao.AcmSequenceResetDao;
import com.armedia.acm.services.sequence.dao.AcmSequenceRegistryUsedDao;
import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.generator.AcmSequenceGeneratorManager;
import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.model.AcmSequenceEntityId;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;
import com.armedia.acm.services.sequence.model.AcmSequenceResetId;

import com.armedia.acm.services.sequence.model.AcmSequenceRegistryUsed;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.FlushModeType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    private String packagesToScan;

    private AcmSequenceGeneratorManager sequenceGeneratorManager;

    private AcmSequenceRegistryUsedDao usedSequenceRegistryDao;

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

    @Override
    public AcmSequenceEntity getNextGeneratedSequence(AcmSequenceEntity sequenceEntity, AcmSequencePart sequencePart)
    {
            AcmSequenceEntity toUpdate = new AcmSequenceEntity();
            toUpdate.setSequenceName(sequenceEntity.getSequenceName());
            toUpdate.setSequencePartName(sequenceEntity.getSequencePartName());
            toUpdate.setSequencePartValue(sequenceEntity.getSequencePartValue() + sequencePart.getSequenceIncrementSize());

            return toUpdate;
    }

    @Override
    public void validateSequence(AcmSequenceEntity acmSequenceEntity) throws AcmAppErrorJsonMsg, AcmSequenceException
    {

        Object[] packages = Arrays.stream(packagesToScan.split(","))
                .map(it -> StringUtils.substringBeforeLast(it, ".*"))
                .toArray();

        Set<Field> fieldsAnnotatedWith = new Reflections(packages, new FieldAnnotationsScanner())
                .getFieldsAnnotatedWith(AcmSequence.class);

        for (Field field : fieldsAnnotatedWith)
        {
            if (field.getAnnotation(AcmSequence.class).sequenceName().equals(acmSequenceEntity.getSequenceName()))
            {
                Class clazz = field.getDeclaringClass();
                String fieldName = field.getName();

                String sequenceName = field.getAnnotation(AcmSequence.class).sequenceName();

                String generatedSequence = getSequenceGeneratorManager().getGenerateValue(sequenceName, clazz,
                        acmSequenceEntity);

                List<Object> result = getSequenceDao().getUsedSequenceObject(clazz, fieldName, generatedSequence);
                if (!result.isEmpty())
                {
                    throw new AcmAppErrorJsonMsg("Sequence number has already been used", null,
                            "existingSequence", null);
                }
            }
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
     * @see com.armedia.acm.services.sequence.service.AcmSequenceService#getAndUpdateSequenceRegistry(java.lang.String,
     * java.lang.String)
     */
    @Override
    public AcmSequenceRegistry getAndUpdateSequenceRegistry(String sequenceName, String sequencePartName,
           FlushModeType flushModeType) throws AcmSequenceException
    {
        log.info("Getting Sequence Registry for [{}] [{}]", sequenceName, sequencePartName);
        try
        {
            AcmSequenceRegistry sequenceRegistry =  getSequenceRegistryDao().getSequenceRegistry(sequenceName, sequencePartName,
                    flushModeType);
            return sequenceRegistry;
        }
        catch (TransactionSystemException e) {
            //we catch this TransactionSystemException caused by long wait for the pessimistic lock ,
            // because we want to continue and generate new sequence from sequence entity
            // if doesn't succeed to get the lock and look for unused sequence in the registry
            log.debug("Error updating sequence registry", e);
            return null;
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to get Sequence Registry for [%s] [%s]", sequenceName, sequencePartName), e);
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
    public void updateSequenceRegistryAsUnused(String sequenceValue) throws AcmSequenceException
    {
        log.info("Moving Sequence from Used Sequence Registry to Unused Sequence Registry [{}]", sequenceValue);
        try
        {
            //looking for sequence in Used Sequence Registry
            AcmSequenceRegistryUsed acmSequenceRegistryUsed = getUsedSequenceRegistryDao().getUsedSequenceRegistry(sequenceValue);

            if(acmSequenceRegistryUsed != null){
                AcmSequenceRegistry sequenceRegistry = new AcmSequenceRegistry();
                sequenceRegistry.setSequenceValue(acmSequenceRegistryUsed.getSequenceValue());
                sequenceRegistry.setSequenceName(acmSequenceRegistryUsed.getSequenceName());
                sequenceRegistry.setSequencePartName(acmSequenceRegistryUsed.getSequencePartName());
                sequenceRegistry.setSequencePartValue(acmSequenceRegistryUsed.getSequencePartValue());
                //if such sequence is found move it in the Sequence Registry with other unused sequences
                saveSequenceRegistry(sequenceRegistry);
                // and remove it from Used Sequence Registry
                getUsedSequenceRegistryDao().removeUsedSequenceRegistry(sequenceValue);
            }

        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Moving sequence didn't succeed [%s]", sequenceValue), e);
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
    @Transactional(propagation = Propagation.SUPPORTS)
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

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public void removeUsedSequenceRegistry(String sequenceValue) throws AcmSequenceException {
        log.info("Removing sequence [{}] from Used Sequence Registry", sequenceValue);
        try
        {
            getUsedSequenceRegistryDao().removeUsedSequenceRegistry(sequenceValue);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to remove sequence [%s] from Used Sequence Registry", sequenceValue),
                    e);

        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Integer removeUsedSequenceRegistry(String sequenceName, String sequencePartName) throws AcmSequenceException
    {
        log.info("Removing Sequence Registry for [{}] [{}]", sequenceName, sequencePartName);
        try
        {
            return getUsedSequenceRegistryDao().removeUsedSequenceRegistry(sequenceName, sequencePartName);
        }
        catch (Exception e)
        {
            throw new AcmSequenceException(
                    String.format("Unable to remove Sequence Registry for [%s] [%s]", sequenceName, sequencePartName), e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AcmSequenceRegistryUsed saveUsedSequenceRegistry(AcmSequenceRegistryUsed sequenceRegistry) throws AcmSequenceException
    {
        log.info("Saving sequence in to Used Sequence Registry for [{}] [{}] [{}]", sequenceRegistry.getSequenceValue(),
                sequenceRegistry.getSequenceName(), sequenceRegistry.getSequencePartName());
        try
        {
            return getUsedSequenceRegistryDao().save(sequenceRegistry);
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

    public String getPackagesToScan()
    {
        return packagesToScan;
    }

    public void setPackagesToScan(String packagesToScan)
    {
        this.packagesToScan = packagesToScan;
    }

    public AcmSequenceGeneratorManager getSequenceGeneratorManager()
    {
        return sequenceGeneratorManager;
    }

    public void setSequenceGeneratorManager(AcmSequenceGeneratorManager sequenceGeneratorManager)
    {
        this.sequenceGeneratorManager = sequenceGeneratorManager;
    }

    public AcmSequenceRegistryUsedDao getUsedSequenceRegistryDao()
    {
        return usedSequenceRegistryDao;
    }

    public void setUsedSequenceRegistryDao(AcmSequenceRegistryUsedDao usedSequenceRegistryDao)
    {
        this.usedSequenceRegistryDao = usedSequenceRegistryDao;
    }
}
