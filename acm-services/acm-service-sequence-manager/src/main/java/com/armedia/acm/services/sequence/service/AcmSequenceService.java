package com.armedia.acm.services.sequence.service;

import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public interface AcmSequenceService
{
    // Sequence Entity
    public AcmSequenceEntity saveSequenceEntity(AcmSequenceEntity sequenceEntity);

    public AcmSequenceEntity getSequenceEntity(String sequenceName, String sequencePartName);

    public AcmSequenceEntity updateSequenceEntity(AcmSequenceEntity sequenceEntity, AcmSequencePart sequencePart, Boolean isReset);

    // Sequence Reset
    public AcmSequenceReset saveSequenceReset(AcmSequenceReset sequenceReset);

    public List<AcmSequenceReset> getSequenceResetList(String sequenceName, String sequencePartName, Boolean resetExecutedFlag);

    public AcmSequenceReset updateSequenceReset(AcmSequenceReset sequenceReset);

    // Sequence Registry
    public AcmSequenceRegistry saveSequenceRegistry(AcmSequenceRegistry sequenceRegistry);

    public List<AcmSequenceRegistry> getSequenceRegistryList(String sequenceName, String sequencePartName,
            Boolean sequencePartValueUsedFlag);

    public Integer updateSequenceRegistryAsUnused(String sequenceValue);

    public Integer removeSequenceRegistry(String sequenceValue);

    public Integer removeSequenceRegistry(String sequenceName, String sequencePartName);
}
