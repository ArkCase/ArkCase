package com.armedia.acm.services.sequence.generator;

import com.armedia.acm.services.sequence.model.AcmSequencePart;

import java.util.Map;
import java.util.UUID;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmUUIDSequenceGenerator implements AcmSequenceGenerator
{
    private AcmSequenceGeneratorManager sequenceGeneratorManager;

    public void init()
    {
        getSequenceGeneratorManager().register("UUID", this);
    }

    /*
     * (non-Javadoc)
     * @see com.armedia.acm.services.sequence.generator.AcmSequenceGenerator#generatePartValue(java.lang.String,
     * com.armedia.acm.services.sequence.model.AcmSequencePart, java.lang.Object, java.util.Map)
     */
    @Override
    public String generatePartValue(String sequenceName, AcmSequencePart sequencePart, Object object,
            Map<String, Long> autoincrementPartNameToValue)
    {
        return UUID.randomUUID().toString();
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

}
