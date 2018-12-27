package com.armedia.acm.services.sequence.generator;

import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;
import com.armedia.acm.services.sequence.service.AcmSequenceService;

import javax.persistence.FlushModeType;

import java.time.LocalDateTime;
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
            LocalDateTime currentDateTime = LocalDateTime.now();
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
                    newSequenceReset.setResetDate(sequenceReset.getResetDate().plusDays(sequenceReset.getResetRepeatablePeriod()));
                    getSequenceService().saveSequenceReset(newSequenceReset);
                }
            }
        }

        // Save sequence entity with reset value
        AcmSequenceEntity savedSequenceEntity = getSequenceService().updateSequenceEntity(sequenceEntity, sequencePart, resetFlag);

        autoincrementPartNameToValue.put(sequencePart.getSequencePartName(), savedSequenceEntity.getSequencePartValue());
        return savedSequenceEntity.getSequencePartValue();
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
