package com.armedia.acm.services.sequence.generator;

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
import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceConstants;
import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistryUsed;
import com.armedia.acm.services.sequence.service.AcmSequenceService;

import javax.persistence.FlushModeType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmAutoincrementSequenceGenerator implements AcmSequenceGenerator
{
    private AcmSequenceGeneratorManager sequenceGeneratorManager;

    private AcmSequenceService sequenceService;

    public void init()
    {
        getSequenceGeneratorManager().register("AUTOINCREMENT", this);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.generator.AcmSequenceGenerator#generatePartValue(java.lang.String,
     * com.armedia.acm.services.sequence.model.AcmSequencePart, java.lang.Object, java.util.Map)
     */
    @Override
    public String generatePartValue(String sequenceName, AcmSequencePart sequencePart, Object object,
           AcmSequenceRegistryUsed usedSequenceRegistry) throws AcmSequenceException
    {
        String autoIncrementPartValue = "";
        Long nextValue = 0L;
        usedSequenceRegistry.setSequenceName(sequenceName);
        usedSequenceRegistry.setSequencePartName(sequencePart.getSequencePartName());
        AcmSequenceEntity sequenceEntity = getSequenceService().getSequenceEntity(sequenceName, sequencePart.getSequencePartName(),
                FlushModeType.COMMIT);
        // Create and use new sequence if not exists
        if (sequenceEntity == null)
        {
            nextValue = createSequence(sequenceName, sequencePart, usedSequenceRegistry);
        }
        else
        {
            // Reset and use start sequence value if reset conditions are met
            List<AcmSequenceReset> sequenceResetList = getSequenceService().getSequenceResetList(sequenceName,
                    sequencePart.getSequencePartName(), Boolean.FALSE, FlushModeType.COMMIT);

            if (sequenceResetList != null && !sequenceResetList.isEmpty())
            {
                nextValue = resetSequence(sequenceResetList, sequenceEntity, sequencePart, usedSequenceRegistry);
            }
            else
            {
                // Check for unused sequence
                if (sequencePart.getSequenceFillBlanks() != null && sequencePart.getSequenceFillBlanks())
                {
                    AcmSequenceRegistry sequenceRegistry = getSequenceService()
                            .getAndUpdateSequenceRegistry(sequenceName, sequencePart.getSequencePartName(), FlushModeType.COMMIT);
                    if (sequenceRegistry != null)
                    {
                        nextValue = updateSequenceRegistry(sequenceRegistry, sequencePart, usedSequenceRegistry);
                    }
                    else
                    {
                        nextValue = updateSequence(sequenceEntity, sequenceName, sequencePart, usedSequenceRegistry);
                    }
                }
                else
                {
                    nextValue = updateSequence(sequenceEntity, sequenceName, sequencePart, usedSequenceRegistry);
                }
            }
        }

        if (sequencePart.getSequenceNumberLength() != null && sequencePart.getSequenceNumberLength() > 0)
        {
            autoIncrementPartValue += String.format("%0" + sequencePart.getSequenceNumberLength() + "d", nextValue);
        }
        else
        {
            autoIncrementPartValue += nextValue;
        }

        return autoIncrementPartValue;
    }

    @Override
    public String getGeneratePartValue(String sequenceName, AcmSequencePart sequencePart, Object object,
                                       AcmSequenceRegistryUsed usedSequenceRegistry, AcmSequenceEntity acmSequenceEntity)
    {
        String autoIncrementPartValue = "";

        Long nextValue = 0L;

        nextValue = getNextGeneratedSequence(acmSequenceEntity, sequenceName, sequencePart, usedSequenceRegistry);

        if (sequencePart.getSequenceNumberLength() != null && sequencePart.getSequenceNumberLength() > 0)
        {
            autoIncrementPartValue += String.format("%0" + sequencePart.getSequenceNumberLength() + "d", nextValue);
        }
        else
        {
            autoIncrementPartValue += nextValue;
        }

        return autoIncrementPartValue;
    }

    private Long createSequence(String sequenceName, AcmSequencePart sequencePart, AcmSequenceRegistryUsed usedSequenceRegistry)
            throws AcmSequenceException
    {
        AcmSequenceEntity sequenceEntity = new AcmSequenceEntity();
        sequenceEntity.setSequenceName(sequenceName);
        sequenceEntity.setSequencePartName(sequencePart.getSequencePartName());
        sequenceEntity.setSequencePartValue(Long.valueOf(sequencePart.getSequenceStartNumber() + sequencePart.getSequenceIncrementSize()));
        AcmSequenceEntity saved = getSequenceService().saveSequenceEntity(sequenceEntity);
        Long partValue = saved.getSequencePartValue();
        usedSequenceRegistry.setSequencePartValue(saved.getSequencePartValue());
        return partValue;
    }

    private Long updateSequence(AcmSequenceEntity sequenceEntity, String sequenceName, AcmSequencePart sequencePart,
            AcmSequenceRegistryUsed usedSequenceRegistry) throws AcmSequenceException
    {
        AcmSequenceEntity updated = getSequenceService().updateSequenceEntity(sequenceEntity, sequencePart, false);
        Long partValue = updated.getSequencePartValue();
        usedSequenceRegistry.setSequencePartValue(updated.getSequencePartValue());
        return partValue;
    }

    private Long getNextGeneratedSequence(AcmSequenceEntity sequenceEntity, String sequenceName, AcmSequencePart sequencePart,
            AcmSequenceRegistryUsed usedSequenceRegistry)
    {
        AcmSequenceEntity updated = getSequenceService().getNextGeneratedSequence(sequenceEntity, sequencePart);
        Long partValue = updated.getSequencePartValue();
        usedSequenceRegistry.setSequencePartValue(updated.getSequencePartValue());
        return partValue;
    }

    private Long updateSequenceRegistry(AcmSequenceRegistry sequenceRegistry, AcmSequencePart sequencePart,
            AcmSequenceRegistryUsed usedSequenceRegistry) throws AcmSequenceException
    {
        Long partValue = sequenceRegistry.getSequencePartValue();
        usedSequenceRegistry.setSequenceValue(sequenceRegistry.getSequenceValue());
        usedSequenceRegistry.setSequencePartValue(sequenceRegistry.getSequencePartValue());
        return partValue;
    }

    private Long resetSequence(List<AcmSequenceReset> sequenceResetList, AcmSequenceEntity sequenceEntity, AcmSequencePart sequencePart,
            AcmSequenceRegistryUsed usedSequenceRegistry) throws AcmSequenceException
    {

        boolean resetFlag = false;
        for (AcmSequenceReset sequenceReset : sequenceResetList)
        {
            LocalDateTime currentDateTime = LocalDateTime.now(ZoneOffset.UTC);
            if (currentDateTime.isAfter(sequenceReset.getResetDate()))
            {
                resetFlag = true;
                // Remove unused sequences from registry, not needed anymore since we reset the respective sequence
                getSequenceService().removeSequenceRegistry(sequenceReset.getSequenceName(), sequenceReset.getSequencePartName());
                // Remove used sequences from used sequence registry if there is any left, not needed anymore since we reset the respective sequence
                getSequenceService().removeUsedSequenceRegistry(sequenceReset.getSequenceName(), sequenceReset.getSequencePartName());

                // Update sequence reset configuration with executed time and executed flag
                sequenceReset.setResetExecutedDate(currentDateTime);
                sequenceReset.setResetExecutedFlag(Boolean.TRUE);
                getSequenceService().updateSequenceReset(sequenceReset);

                // Create new sequence reset configuration for repeatable reset
                if (sequenceReset.getResetRepeatableFlag().equals(Boolean.TRUE))
                {
                    AcmSequenceReset newSequenceReset = new AcmSequenceReset();
                    newSequenceReset.setSequenceName(sequenceReset.getSequenceName());
                    newSequenceReset.setSequencePartName(sequenceReset.getSequencePartName());
                    newSequenceReset.setResetRepeatableFlag(sequenceReset.getResetRepeatableFlag());
                    newSequenceReset.setResetRepeatablePeriod(sequenceReset.getResetRepeatablePeriod());
                    if (sequenceReset.getResetRepeatablePeriod() != null)
                    {
                        newSequenceReset.setResetDate(calculateNewResetDateTime(sequenceReset, currentDateTime));
                    }
                    getSequenceService().saveSequenceReset(newSequenceReset);
                }
            }
        }

        // Save sequence entity with reset value
        AcmSequenceEntity savedSequenceEntity = getSequenceService().updateSequenceEntity(sequenceEntity, sequencePart, resetFlag);
        usedSequenceRegistry.setSequencePartValue(savedSequenceEntity.getSequencePartValue());
        return savedSequenceEntity.getSequencePartValue();
    }

    private LocalDateTime calculateNewResetDateTime(AcmSequenceReset sequenceReset, LocalDateTime currentDateTime)
    {
        LocalDateTime newResetDateTime = null;
        switch (sequenceReset.getResetRepeatablePeriod())
        {
        case AcmSequenceConstants.SEQUENCE_RESET_YEARLY:
            newResetDateTime = sequenceReset.getResetDate().plusYears(1);
            while (currentDateTime.isAfter(newResetDateTime))
            {
                newResetDateTime = newResetDateTime.plusYears(1);
            }
            break;
        case AcmSequenceConstants.SEQUENCE_RESET_MONTHLY:
            newResetDateTime = sequenceReset.getResetDate().plusMonths(1);
            while (currentDateTime.isAfter(newResetDateTime))
            {
                newResetDateTime = newResetDateTime.plusMonths(1);
            }
            break;
        case AcmSequenceConstants.SEQUENCE_RESET_WEEKLY:
            newResetDateTime = sequenceReset.getResetDate().plusWeeks(1);
            while (currentDateTime.isAfter(newResetDateTime))
            {
                newResetDateTime = newResetDateTime.plusWeeks(1);
            }
            break;
        default:
            if (sequenceReset.getResetRepeatablePeriod() > 0)
            {
                newResetDateTime = sequenceReset.getResetDate().plusDays(sequenceReset.getResetRepeatablePeriod());
                while (currentDateTime.isAfter(newResetDateTime))
                {
                    newResetDateTime = newResetDateTime.plusDays(sequenceReset.getResetRepeatablePeriod());
                }
            }
        }
        return newResetDateTime;
    }

    /**
     * @return the sequenceGeneratorManager
     */
    public AcmSequenceGeneratorManager getSequenceGeneratorManager()
    {
        return sequenceGeneratorManager;
    }

    /**
     * @param sequenceGeneratorManager
     *            the sequenceGeneratorManager to set
     */
    public void setSequenceGeneratorManager(AcmSequenceGeneratorManager sequenceGeneratorManager)
    {
        this.sequenceGeneratorManager = sequenceGeneratorManager;
    }

    /**
     * @return the sequenceService
     */
    public AcmSequenceService getSequenceService()
    {
        return sequenceService;
    }

    /**
     * @param sequenceService
     *            the sequenceService to set
     */
    public void setSequenceService(AcmSequenceService sequenceService)
    {
        this.sequenceService = sequenceService;
    }

}
