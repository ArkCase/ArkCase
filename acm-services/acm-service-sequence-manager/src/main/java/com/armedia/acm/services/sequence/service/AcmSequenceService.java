package com.armedia.acm.services.sequence.service;

import com.armedia.acm.services.sequence.exception.AcmSequenceException;

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

import com.armedia.acm.services.sequence.model.AcmSequenceEntity;
import com.armedia.acm.services.sequence.model.AcmSequencePart;
import com.armedia.acm.services.sequence.model.AcmSequenceRegistry;
import com.armedia.acm.services.sequence.model.AcmSequenceReset;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import javax.persistence.FlushModeType;
import javax.persistence.OptimisticLockException;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public interface AcmSequenceService
{
    // Sequence Entity
    public AcmSequenceEntity saveSequenceEntity(AcmSequenceEntity sequenceEntity) throws AcmSequenceException;

    public AcmSequenceEntity getSequenceEntity(String sequenceName, String sequencePartName, FlushModeType flushModeType)
            throws AcmSequenceException;

    @Retryable(maxAttempts = 30, value = OptimisticLockException.class, backoff = @Backoff(delay = 100))
    public AcmSequenceEntity updateSequenceEntity(AcmSequenceEntity sequenceEntity, AcmSequencePart sequencePart, Boolean isReset)
            throws AcmSequenceException;

    public AcmSequenceEntity updateSequenceEntity(AcmSequenceEntity acmSequenceEntity)
            throws AcmSequenceException;

    // Sequence Reset
    public AcmSequenceReset saveSequenceReset(AcmSequenceReset sequenceReset) throws AcmSequenceException;

    public List<AcmSequenceReset> getSequenceResetList(String sequenceName, String sequencePartName, Boolean resetExecutedFlag,
            FlushModeType flushModeType)
            throws AcmSequenceException;

    public List<AcmSequenceReset> getSequenceResetList(String sequenceName, String sequencePartName) throws AcmSequenceException;

    public AcmSequenceReset updateSequenceReset(AcmSequenceReset sequenceReset) throws AcmSequenceException;

    public void deleteSequenceReset(AcmSequenceReset sequenceReset) throws AcmSequenceException;

    // Sequence Registry
    public AcmSequenceRegistry saveSequenceRegistry(AcmSequenceRegistry sequenceRegistry) throws AcmSequenceException;

    public List<AcmSequenceRegistry> getSequenceRegistryList(String sequenceName, String sequencePartName,
            Boolean sequencePartValueUsedFlag, FlushModeType flushModeType) throws AcmSequenceException;

    public List<AcmSequenceRegistry> getSequenceRegistryList() throws AcmSequenceException;

    public Integer updateSequenceRegistryAsUnused(String sequenceValue) throws AcmSequenceException;

    public Integer removeSequenceRegistry(String sequenceValue) throws AcmSequenceException;

    public Integer removeSequenceRegistry(String sequenceName, String sequencePartName) throws AcmSequenceException;
}
