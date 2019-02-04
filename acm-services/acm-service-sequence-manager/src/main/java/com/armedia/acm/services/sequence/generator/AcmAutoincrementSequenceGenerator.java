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
import com.armedia.acm.services.sequence.service.AcmSequenceService;

import javax.persistence.FlushModeType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

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
            Map<String, Long> autoincrementPartNameToValue) throws AcmSequenceException
    {
        String autoIncrementPartValue = "";

        Long nextValue = 0L;
        AcmSequenceEntity sequenceEntity = getSequenceService().getSequenceEntity(sequenceName, sequencePart.getSequencePartName(),
                FlushModeType.COMMIT);
        // Create and use new sequence if not exists
        if (sequenceEntity == null)
        {
            nextValue = createSequence(sequenceName, sequencePart, autoincrementPartNameToValue);
        }
        else
        {
            // Reset and use start sequence value if reset conditions are met
            List<AcmSequenceReset> sequenceResetList = getSequenceService().getSequenceResetList(sequenceName,
                    sequencePart.getSequencePartName(), Boolean.FALSE, FlushModeType.COMMIT);

            if (sequenceResetList != null && !sequenceResetList.isEmpty())
            {
                nextValue = resetSequence(sequenceResetList, sequenceEntity, sequencePart, autoincrementPartNameToValue);
            }
            else
            {
                // Check for unused sequences
                if (sequencePart.getSequenceFillBlanks() != null && sequencePart.getSequenceFillBlanks())
                {
                    List<AcmSequenceRegistry> sequenceRegistryList = getSequenceService()
                            .getSequenceRegistryList(sequenceName, sequencePart.getSequencePartName(), Boolean.FALSE, FlushModeType.COMMIT);
                    if (sequenceRegistryList != null && !sequenceRegistryList.isEmpty())
                    {
                        nextValue = getSequenceFromRegistry(sequenceRegistryList, sequencePart, autoincrementPartNameToValue);
                    }
                    else
                    {
                        nextValue = updateSequence(sequenceEntity, sequenceName, sequencePart, autoincrementPartNameToValue);
                    }
                }
                else
                {
                    nextValue = updateSequence(sequenceEntity, sequenceName, sequencePart, autoincrementPartNameToValue);
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

    private Long createSequence(String sequenceName, AcmSequencePart sequencePart, Map<String, Long> autoincrementPartNameToValue)
            throws AcmSequenceException
    {
        AcmSequenceEntity sequenceEntity = new AcmSequenceEntity();
        sequenceEntity.setSequenceName(sequenceName);
        sequenceEntity.setSequencePartName(sequencePart.getSequencePartName());
        sequenceEntity.setSequencePartValue(Long.valueOf(sequencePart.getSequenceStartNumber() + sequencePart.getSequenceIncrementSize()));
        AcmSequenceEntity saved = getSequenceService().saveSequenceEntity(sequenceEntity);
        Long partValue = saved.getSequencePartValue();
        autoincrementPartNameToValue.put(sequencePart.getSequencePartName(), partValue);
        return partValue;
    }

    private Long updateSequence(AcmSequenceEntity sequenceEntity, String sequenceName, AcmSequencePart sequencePart,
            Map<String, Long> autoincrementPartNameToValue) throws AcmSequenceException
    {
        AcmSequenceEntity updated = getSequenceService().updateSequenceEntity(sequenceEntity, sequencePart, false);
        Long partValue = updated.getSequencePartValue();
        autoincrementPartNameToValue.put(sequencePart.getSequencePartName(), partValue);
        return partValue;
    }

    private Long getSequenceFromRegistry(List<AcmSequenceRegistry> sequenceRegistryList, AcmSequencePart sequencePart,
            Map<String, Long> autoincrementPartNameToValue) throws AcmSequenceException
    {
        Long partValue = sequenceRegistryList.get(0).getSequencePartValue();
        autoincrementPartNameToValue.put(sequencePart.getSequencePartName(), partValue);
        getSequenceService().removeSequenceRegistry(sequenceRegistryList.get(0).getSequenceValue());
        return partValue;
    }

    private Long resetSequence(List<AcmSequenceReset> sequenceResetList, AcmSequenceEntity sequenceEntity, AcmSequencePart sequencePart,
            Map<String, Long> autoincrementPartNameToValue) throws AcmSequenceException
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

        autoincrementPartNameToValue.put(sequencePart.getSequencePartName(), savedSequenceEntity.getSequencePartValue());
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
