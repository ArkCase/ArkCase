package com.armedia.acm.services.sequence.generator;

import com.armedia.acm.services.sequence.exception.AcmSequenceException;
import com.armedia.acm.services.sequence.model.AcmSequencePart;

import java.util.Map;

/**
 * @author sasko.tanaskoski
 *
 */
public interface AcmSequenceGenerator
{

    public String generatePartValue(String sequenceName, AcmSequencePart sequencePart, Object object,
            Map<String, Long> autoincrementPartNameToValue) throws AcmSequenceException;

}
