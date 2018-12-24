package com.armedia.acm.services.sequence.service;

import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequenceConfiguration;

import java.util.List;

/**
 * @author sasko.tanaskoski
 *
 */
public interface AcmSequenceConfigurationService
{
    public List<AcmSequenceConfiguration> saveSequenceConfiguration(List<AcmSequenceConfiguration> sequenceConfigurationList)
            throws AcmSequenceException;

    public List<AcmSequenceConfiguration> getSequenceConfiguration() throws AcmSequenceException;
}
