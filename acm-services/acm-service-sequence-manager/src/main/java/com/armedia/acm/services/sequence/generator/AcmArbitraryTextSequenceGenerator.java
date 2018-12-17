package com.armedia.acm.services.sequence.generator;

import com.armedia.acm.services.sequence.model.AcmSequencePart;

import java.util.Map;

/**
 * @author sasko.tanaskoski
 *
 */
public class AcmArbitraryTextSequenceGenerator implements AcmSequenceGenerator
{
    private AcmSequenceGeneratorManager sequenceGeneratorManager;

    public void init()
    {
        getSequenceGeneratorManager().register("ARBITRARY_TEXT", this);
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
        return sequencePart.getSequenceArbitraryText();
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
